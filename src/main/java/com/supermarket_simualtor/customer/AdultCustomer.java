package com.supermarket_simualtor.customer;

import com.supermarket_simualtor.customer.storage.Bonuses;
import com.supermarket_simualtor.customer.storage.Card;
import com.supermarket_simualtor.customer.storage.Wallet;

public class AdultCustomer extends AbstractCustomer {
    public AdultCustomer(String name, int age, Wallet wallet, Card card, Bonuses bonuses) {
        super(name, age, wallet, card, bonuses);
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
