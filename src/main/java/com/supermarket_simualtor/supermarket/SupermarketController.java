package com.supermarket_simualtor.supermarket;

import com.supermarket_simualtor.cash_desk.CashDesk;
import com.supermarket_simualtor.product.Product;

import java.util.List;
import java.util.Set;

public interface SupermarketController {
    Set<String> getAssortment();

    List<CashDesk> getDesks();

    Product tryTakeProduct(String name) throws ProductNotFoundException;

    long getQuantityFor(String name) throws ProductNotFoundException;
}
