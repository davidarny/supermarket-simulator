package com.supermarket_simualtor.cash_desk;

import com.supermarket_simualtor.customer.Customer;
import com.supermarket_simualtor.product.Product;
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

    public void serveCustomer(@NotNull Customer customer) {
        try {
            lock.lock();
            val products = customer.getBasket().takeAll();
            val name = customer.getName();
            val disallowed = removeDisallowedForCustomer(customer, products);
            logDisallowed(name, disallowed);
            double total = countTotalCost(products, name);
            logTotalCost(products, name, total);
        } finally {
            lock.unlock();
        }
    }

    private void logTotalCost(List<Product> products, String name, double total) {
        if (total > 0) {
            logger.info("{} bought total {} of item for {}$", name, products.size(), friendlyDouble(total));
        }
    }

    private String friendlyDouble(double total) {
        return String.format("%.2f", total);
    }

    private double countTotalCost(List<Product> products, String name) {
        double total = 0;
        for (val entry : products.stream().collect(Collectors.groupingBy(Product::getName)).entrySet()) {
            val item = entry.getKey();
            val price = pricing.get(item);
            val quantity = entry.getValue().size();
            double cost = price * quantity;
            total += cost;
            logger.info("{} paying {} of {} for {}$", name, quantity, item, friendlyDouble(cost));
        }
        return total;
    }

    private void logDisallowed(String name, Set<String> disallowed) {
        for (val product : disallowed) {
            logger.info("{} disallowed to take {}", name, product);
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
