package com.supermarket_simualtor.customer.payment;


import com.supermarket_simualtor.customer.storage.MoneyStorage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BasicPayment implements Payment {
    private final MoneyStorage storage;

    @Override
    public synchronized void pay(double money) {
        storage.take(money);
    }
}
