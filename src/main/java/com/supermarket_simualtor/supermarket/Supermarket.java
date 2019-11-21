package com.supermarket_simualtor.supermarket;

import com.supermarket_simualtor.cash_desk.CashDesk;
import com.supermarket_simualtor.customer.Customer;
import com.supermarket_simualtor.product.Product;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class Supermarket implements SupermarketController, SupermarketAcceptor {
    private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    private final DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private final String name;

    @Getter
    private final List<CashDesk> desks;
    private final SupermarketRepository repository;

    @Override
    public void accept(@NotNull Customer customer) {
        val date = new Date();
        logger.info("{} visited {} at {}", customer.getName(), name, format.format(date));
        customer.visit(this);
        logger.info("{} leaving {}", customer.getName(), name);

    }

    @Override
    public Set<String> getAssortment() {
        return repository.getAssortment();
    }

    @Override
    public Product tryTakeProduct(String name) throws ProductNotFoundException {
        return repository.takeByName(name);
    }

    @Override
    public long getQuantityFor(String name) throws ProductNotFoundException {
        return repository.getQuantityFor(name);
    }
}
