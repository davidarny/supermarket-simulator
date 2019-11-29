package com.supermarket_simualtor.customer;

import com.supermarket_simualtor.customer.wallet.Wallet;

public class AdultCustomer extends AbstractCustomer {
    public AdultCustomer(String name, int age, Wallet wallet) {
        super(name, age, wallet);
    }

    @Override
    public boolean isRetired() {
        return false;
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
