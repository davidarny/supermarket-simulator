package com.supermarket_simualtor.customer.storage;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class BasicMoneyStorage implements MoneyStorage {
    private double total;

    @Override
    public double get() {
        return total;
    }

    @Override
    public void take(double money) {
        total -= money;
    }

    @Override
    public synchronized void add(double money) {
        assert money > 0;
        total += money;
    }
}
