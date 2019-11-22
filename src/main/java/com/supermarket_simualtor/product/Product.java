package com.supermarket_simualtor.product;

import com.supermarket_simualtor.customer.Customer;
import lombok.Value;

@Value
public class Product implements Comparable<Product>, ProductPermissions {
    private final long id;

    private final String name;

    private final ProductPermissions permissions;

    @Override
    public int compareTo(Product other) {
        return Integer.compare((int) id, (int) other.id);
    }

    @Override
    public boolean allowedForChild(Customer customer) {
        return permissions.allowedForChild(customer);
    }
}
