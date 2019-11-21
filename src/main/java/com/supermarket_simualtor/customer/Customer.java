package com.supermarket_simualtor.customer;

import com.supermarket_simualtor.basket.Basket;
import com.supermarket_simualtor.supermarket.SupermarketController;

public interface Customer {
    String getName();

    Basket getBasket();

    void visit(SupermarketController supermarket);
}
