package com.supermarket_simualtor.report;

import com.supermarket_simualtor.utils.StringUtils;

public class Report {
    private double income = 0.0;

    public void addIncome(double income) {
        this.income += income;
    }

    @Override
    public String toString() {
        return StringUtils.friendlyDouble(income);
    }
}
