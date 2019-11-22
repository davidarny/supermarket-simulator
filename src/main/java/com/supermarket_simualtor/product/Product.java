package com.supermarket_simualtor.product;

import com.supermarket_simualtor.customer.Customer;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class Product implements Comparable<Product> {
    private final long id;

    private final String name;

    private final boolean weighted;

    private final ProductPermissions permissions;

    private double weight;

    public Product(long id) {
        this.id = id;
        name = null;
        weight = 0.0;
        weighted = false;
        permissions = null;
    }

    public Product(long id, String name, double weight, ProductPermissions permissions) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        weighted = true;
        this.permissions = permissions;
    }

    public Product(long id, String name, ProductPermissions permissions) {
        this.id = id;
        this.name = name;
        weight = 0.0;
        weighted = false;
        this.permissions = permissions;
    }

    public Product take(double weight) throws NonWeightedTakeException {
        if (!weighted) {
            throw new NonWeightedTakeException("Cannot take some of " + name);
        }
        try {
            var next = this.weight - weight;
            if (next < 0) {
                next = 0;
            }
            return new Product(id, name, next, permissions);
        } finally {
            this.weight = weight;
        }
    }

    @Override
    public int compareTo(@NotNull Product other) {
        return Integer.compare((int) id, (int) other.id);
    }

    public boolean allowedForChild(Customer customer) {
        assert permissions != null;
        return permissions.allowedForChild(customer);
    }
}
