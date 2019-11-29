package com.supermarket_simualtor.customer;

public class ChildCustomer extends AbstractCustomer {
    public ChildCustomer(String name, int age) {
        super(name, age);
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
