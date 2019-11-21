package com.supermarket_simualtor.supermarket;

import com.supermarket_simualtor.product.Product;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class SupermarketRepository {
    private final ConcurrentSkipListSet<Product> assortment = new ConcurrentSkipListSet<>();
    private final ConcurrentHashMap<String, ProductMeta> waybill = new ConcurrentHashMap<>();

    private final ReentrantLock lock = new ReentrantLock();

    public SupermarketRepository(List<Product> products) {
        addAll(products);
    }

    public int getCount() {
        try {
            lock.lock();
            return assortment.size();
        } finally {
            lock.unlock();
        }
    }

    public Set<String> getAssortment() {
        try {
            lock.lock();
            return assortment.stream().map(Product::getName).collect(Collectors.toSet());
        } finally {
            lock.unlock();
        }
    }

    Product takeByName(String name) throws ProductNotFoundException {
        try {
            lock.lock();
            val product = getByName(name);
            if (product == null) {
                handleNotFound(name);
            }
            takeProduct(name, product);
            return product;
        } finally {
            lock.unlock();
        }
    }

    long getQuantityFor(String name) throws ProductNotFoundException {
        try {
            lock.lock();
            val product = getByName(name);
            if (product == null) {
                handleNotFound(name);
            }
            val meta = waybill.get(product.getName());
            return meta.ids.size();
        } finally {
            lock.unlock();
        }
    }

    @Nullable
    private Product getByName(String name) {
        if (!waybill.containsKey(name)) {
            return null;
        }
        @NonNull val meta = waybill.get(name);
        val id = meta.ids.stream().min(Long::compareTo).orElseThrow();
        val predicate = new Product(id, null);
        return searchProduct(predicate);
    }

    private void takeProduct(String name, Product product) {
        assortment.remove(product);
        val meta = waybill.get(name);
        meta.remove(product.getId());
        if (meta.ids.size() == 0) {
            waybill.remove(name);
        }
    }

    @Nullable
    private Product searchProduct(Product search) {
        val ceil = assortment.ceiling(search);
        val floor = assortment.floor(search);
        return ceil == floor ? ceil : null;
    }

    private void addAll(List<Product> list) {
        assortment.addAll(list);

        for (val product : list) {
            val name = product.getName();
            val id = (int) product.getId();

            if (waybill.containsKey(name)) {
                @NonNull val meta = waybill.get(name);
                meta.add(id);
                continue;
            }

            val meta = new ProductMeta();
            meta.add(id);
            waybill.put(name, meta);
        }
    }

    private void handleNotFound(String name) throws ProductNotFoundException {
        throw new ProductNotFoundException("Product with name " + name + " not found");
    }


    private static class ProductMeta {
        private final List<Long> ids = new ArrayList<>();

        void add(long id) {
            ids.add(id);
        }

        void remove(long id) {
            ids.remove(id);
        }
    }
}
