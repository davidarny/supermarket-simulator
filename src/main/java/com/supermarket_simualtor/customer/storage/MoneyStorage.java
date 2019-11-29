package com.supermarket_simualtor.customer.storage;

public interface MoneyStorage {
    double get();

    void take(double money);

    void add(double money);
}
