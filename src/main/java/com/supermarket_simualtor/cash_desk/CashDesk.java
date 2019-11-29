package com.supermarket_simualtor.cash_desk;

import com.supermarket_simualtor.bill.Bill;
import com.supermarket_simualtor.customer.Customer;
import com.supermarket_simualtor.product.Product;
import com.supermarket_simualtor.report.Report;
import com.supermarket_simualtor.utils.StringUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CashDesk {
    private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
    private final ReentrantLock lock = new ReentrantLock();

    @Getter
    private final long id;

    private final Map<String, Double> pricing;

    private final Report report;

    public void serveCustomer(@NotNull Customer customer) {
        try {
            lock.lock();
            val products = customer.getBasket().takeAll();
            val name = customer.getName();
            val disallowed = removeDisallowedForCustomer(customer, products);
            logDisallowed(name, disallowed);
            val bill = countTotalCost(products, customer);
            customer.payForBill(bill);
            report.addIncome(bill.getTotal());
            logTotalCost(products, name, bill.getTotal());
        } finally {
            lock.unlock();
        }
    }

    private Bill countTotalCost(List<Product> products, Customer customer) {
        double total = 0;
        double bonuses = 0;
        for (val entry : products.stream().collect(Collectors.groupingBy(Product::getName)).entrySet()) {
            val item = entry.getKey();
            val price = pricing.get(item);
            val quantity = entry.getValue().size();
            double cost = 0.0;
            for (val product : entry.getValue()) {
                double discount = product.discountForRetired(customer);
                bonuses += product.applyBonuses();
                if (product.isWeighted()) {
                    cost += discount * price * (product.getWeight() / 1000);
                } else {
                    cost += discount * price;
                }
            }
            total += cost;
            logPayment(customer.getName(), item, quantity, cost);
        }
        return new Bill(total, bonuses);
    }

    private void logPayment(String name, String item, int quantity, double cost) {
        if (logger.isInfoEnabled()) {
            logger.info("{} paying {} of {} for {}$", name, quantity, item, StringUtils.friendlyDouble(cost));
        }
    }

    private void logDisallowed(String name, Set<String> disallowed) {
        for (val product : disallowed) {
            logger.info("{} disallowed to take {}", name, product);
        }
    }

    private void logTotalCost(List<Product> products, String name, double total) {
        if (total > 0 && logger.isInfoEnabled()) {
            logger.info("{} bought total {} of item for {}$", name, products.size(), StringUtils.friendlyDouble(total));
        }
    }

    private Set<String> removeDisallowedForCustomer(@NotNull Customer customer, List<Product> products) {
        val it = products.iterator();
        val disallowed = new HashSet<String>();
        while (it.hasNext()) {
            val product = it.next();
            if (!product.allowedForChild(customer)) {
                disallowed.add(product.getName());
                it.remove();
            }
        }
        return disallowed;
    }
}
