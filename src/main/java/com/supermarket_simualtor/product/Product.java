package com.supermarket_simualtor.product;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
public class Product implements Comparable<Product> {
    private final long id;
    private final String name;

    @Override
    public int compareTo(@NotNull Product other) {
        return Integer.compare((int) id, (int) other.id);
    }
}
