package com.supermarket_simualtor.customer;

import com.supermarket_simualtor.customer.storage.Bonuses;
import com.supermarket_simualtor.customer.storage.Card;
import com.supermarket_simualtor.customer.storage.Wallet;

public class RetiredCustomer extends AbstractCustomer {
    public RetiredCustomer(String name, int age, Wallet wallet, Card card, Bonuses bonuses) {
        super(name, age, wallet, card, bonuses);
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
