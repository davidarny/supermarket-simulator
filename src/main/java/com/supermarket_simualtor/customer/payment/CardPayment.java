package com.supermarket_simualtor.customer.payment;

import com.supermarket_simualtor.customer.storage.MoneyStorage;

public class CardPayment extends BasicPayment {
    public CardPayment(MoneyStorage storage) {
        super(storage);
    }
}
