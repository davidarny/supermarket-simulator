package com.supermarket_simualtor.customer;

import com.supermarket_simualtor.basket.Basket;
import com.supermarket_simualtor.supermarket.SupermarketController;

public interface Customer {
    String getName();

    Basket getBasket();

    boolean isRetired();

    boolean isChild();

    boolean isAdult();

    void visit(SupermarketController supermarket);
}
