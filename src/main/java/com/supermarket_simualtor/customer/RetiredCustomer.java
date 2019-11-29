package com.supermarket_simualtor.customer;

public class RetiredCustomer extends AbstractCustomer {
    public RetiredCustomer(String name, int age) {
        super(name, age);
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
