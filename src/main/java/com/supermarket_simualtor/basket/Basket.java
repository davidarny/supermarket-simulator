package com.supermarket_simualtor.basket;

import com.supermarket_simualtor.product.Product;
import lombok.val;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Basket {
    private static final int MAX_CAPACITY = 25;

    private final List<Product> products = Collections.synchronizedList(new ArrayList<>());

    public void add(Product product) {
        products.add(product);
    }

    public List<Product> takeAll() {
        val list = new ArrayList<>(products);
        products.clear();
        return list;
    }

    public int freeCapacity() {
        return MAX_CAPACITY - products.size();
    }
}
