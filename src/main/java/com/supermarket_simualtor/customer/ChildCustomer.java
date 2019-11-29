package com.supermarket_simualtor.customer;

import com.supermarket_simualtor.customer.storage.Bonuses;
import com.supermarket_simualtor.customer.storage.Card;
import com.supermarket_simualtor.customer.storage.Wallet;

public class ChildCustomer extends AbstractCustomer {
    public ChildCustomer(String name, int age, Wallet wallet, Card card, Bonuses bonuses) {
        super(name, age, wallet, card, bonuses);
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
