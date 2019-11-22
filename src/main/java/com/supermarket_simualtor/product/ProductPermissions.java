package com.supermarket_simualtor.product;

import com.supermarket_simualtor.customer.Customer;

public interface ProductPermissions {
    boolean allowedForChild(Customer customer);
}
