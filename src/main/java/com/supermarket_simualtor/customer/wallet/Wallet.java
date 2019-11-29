package com.supermarket_simualtor.customer.wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Wallet {
    @Getter
    private double total;
    @Getter
    private double bonuses;

    public boolean hasEnoughCash(double money) {
        return total >= money;
    }

    public boolean hasEnoughCashWithBonuses(double money) {
        return total + bonuses >= money;
    }

    public void addBonuses(double bonuses) {
        this.bonuses += bonuses;
    }

    public void payWithCash(double money) {
        assert hasEnoughCash(money);
        total -= money;
    }

    public void payWithCashAndBonuses(double money) {
        assert hasEnoughCashWithBonuses(money);
        if (money > bonuses) {
            bonuses  = 0;
            money -= bonuses;
        } else {
            bonuses -= money;
        }
        total -= money;
    }
}
