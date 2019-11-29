package com.supermarket_simualtor.supermarket;

import com.supermarket_simualtor.product.Product;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.SynchronousQueue;
import java.util.stream.Collectors;

public class SupermarketRepository {
    private final ConcurrentSkipListSet<Product> assortment = new ConcurrentSkipListSet<>();
    private final ConcurrentHashMap<String, MetaInfo> waybill = new ConcurrentHashMap<>();

    public SupermarketRepository(List<Product> products) {
        addAll(products);
    }

    public synchronized int getCount() {
        return assortment.size();
    }

    public synchronized Set<String> getAssortment() {
        return assortment.stream().map(Product::getName).collect(Collectors.toSet());
    }

    synchronized Product takeByName(String name) throws ProductNotFoundException {
        val product = getByName(name);
        if (product == null) {
            handleNotFound(name);
        }
        takeProduct(product);
        return product;
    }

    synchronized long getQuantityFor(String name) throws ProductNotFoundException {
        val product = getByName(name);
        if (product == null) {
            handleNotFound(name);
        }
        val meta = waybill.get(product.getName());
        return meta.ids.size();
    }

    synchronized void add(Product product) {
        assortment.add(product);

        val name = product.getName();
        val id = (int) product.getId();

        if (waybill.containsKey(name)) {
            val meta = waybill.get(name);
            meta.add(id);
            return;
        }

        val meta = new MetaInfo();
        meta.add(id);
        waybill.put(name, meta);
    }

    @Nullable
    private Product getByName(String name) {
        if (!waybill.containsKey(name)) {
            return null;
        }
        val meta = waybill.get(name);
        val id = meta.ids.get(0);
        assert id != null;
        meta.ids.remove(0);
        val predicate = new Product(id);
        return searchProduct(predicate);
    }

    private void takeProduct(Product product) {
        val name = product.getName();
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
        for (val product : list) {
            add(product);
        }
    }

    private void handleNotFound(String name) throws ProductNotFoundException {
        throw new ProductNotFoundException("Product with name " + name + " not found");
    }


    private static class MetaInfo {
        private final List<Long> ids = Collections.synchronizedList(new ArrayList<>());

        void add(long id) {
            ids.add(id);
        }

        void remove(long id) {
            ids.remove(id);
        }
    }
}
