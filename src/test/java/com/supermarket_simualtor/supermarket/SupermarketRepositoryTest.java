package com.supermarket_simualtor.supermarket;

import com.supermarket_simualtor.product.Product;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SupermarketRepositoryTest {
    @Test
    public void getById_productInMiddle_productFound() throws ProductNotFoundException {
        val repo = createRepository();

        val actual = repo.takeByName("Pepsi");

        assertEquals(1, actual.getId());
        assertEquals("Pepsi", actual.getName());
    }

    @Test
    public void getById_productOnTop_productFound() throws ProductNotFoundException {
        val repo = createRepository();

        val actual = repo.takeByName("Coca Cola");

        assertEquals(0, actual.getId());
        assertEquals("Coca Cola", actual.getName());
    }

    @Test
    public void getById_productAtTheEnd_productFound() throws ProductNotFoundException {
        val repo = createRepository();

        val actual = repo.takeByName("Sprite");

        assertEquals(2, actual.getId());
        assertEquals("Sprite", actual.getName());
    }

    @Test(expected = ProductNotFoundException.class)
    public void getById_nonExistingId_null() throws ProductNotFoundException {
        val repo = createRepository();

        repo.takeByName("FooBar");
    }

    @Test
    public void getByName_sameProductsWithDifferentIds_productFound() throws ProductNotFoundException {
        val repo = createRepository(Collections.singletonList(new Product(4, "Pepsi", null)));

        val actual = repo.takeByName("Pepsi");

        assertEquals(1, actual.getId());
        assertEquals("Pepsi", actual.getName());
    }

    @Test
    public void addAll_emptyRepo_nonEmptyRepo() {
        val repo = createEmptyRepository(Arrays.asList(
            new Product(0, "Coca Cola", null),
            new Product(1, "Pepsi", null),
            new Product(2, "Sprite", null)
        ));

        assertEquals(3, repo.getCount());
    }

    @Test
    public void addAll_nonEmptyRepo_appendToRepo() {
        val repo = createRepository(Arrays.asList(
            new Product(4, "Lays", null),
            new Product(5, "Snickers", null),
            new Product(6, "Twix", null)
        ));

        assertEquals(6, repo.getCount());
    }

    @NotNull
    private SupermarketRepository createRepository() {
        return new SupermarketRepository(Arrays.asList(
            new Product(0, "Coca Cola", null),
            new Product(1, "Pepsi", null),
            new Product(2, "Sprite", null)
        ));
    }

    @NotNull
    private SupermarketRepository createRepository(List<Product> products) {
        val list = new ArrayList<>(Arrays.asList(
            new Product(0, "Coca Cola", null),
            new Product(1, "Pepsi", null),
            new Product(2, "Sprite", null)
        ));
        list.addAll(products);
        return new SupermarketRepository(list);
    }

    @NotNull
    private SupermarketRepository createEmptyRepository(List<Product> products) {
        return new SupermarketRepository(products);
    }
}
