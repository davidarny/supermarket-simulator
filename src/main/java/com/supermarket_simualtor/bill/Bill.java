package com.supermarket_simualtor.bill;

import lombok.Value;

@Value
public class Bill {
    private final double total;
    private final double bonuses;
}
