package com.supermarket_simualtor.customer;

import com.supermarket_simualtor.customer.wallet.Wallet;

public class ChildCustomer extends AbstractCustomer {
    public ChildCustomer(String name, int age, Wallet wallet) {
        super(name, age, wallet);
    }

    @Override
    public boolean isRetired() {
        return false;
    }

    @Override
    public boolean isChild() {
        return true;
    }

    @Override
    public boolean isAdult() {
        return false;
    }
}
