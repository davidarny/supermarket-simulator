package com.supermarket_simualtor.customer.payment;

import com.supermarket_simualtor.customer.storage.MoneyStorage;

public class CashPayment extends BasicPayment {
    public CashPayment(MoneyStorage storage) {
        super(storage);
    }
}
