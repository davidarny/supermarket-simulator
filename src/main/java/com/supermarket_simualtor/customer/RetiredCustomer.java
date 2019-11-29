package com.supermarket_simualtor.customer;

import com.supermarket_simualtor.customer.wallet.Wallet;

public class RetiredCustomer extends AbstractCustomer {
    public RetiredCustomer(String name, int age, Wallet wallet) {
        super(name, age, wallet);
    }

    @Override
    public boolean isRetired() {
        return true;
    }

    @Override
    public boolean isChild() {
        return false;
    }

    @Override
    public boolean isAdult() {
        return true;
    }
}
