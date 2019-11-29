package com.supermarket_simualtor.customer;

public class AdultCustomer extends AbstractCustomer {
    public AdultCustomer(String name, int age) {
        super(name, age);
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
