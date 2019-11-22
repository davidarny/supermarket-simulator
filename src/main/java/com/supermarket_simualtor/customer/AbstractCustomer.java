package com.supermarket_simualtor.customer;

import com.supermarket_simualtor.basket.Basket;
import com.supermarket_simualtor.product.NonWeightedTakeException;
import com.supermarket_simualtor.random.CustomRandom;
import com.supermarket_simualtor.supermarket.ProductNotFoundException;
import com.supermarket_simualtor.supermarket.SupermarketController;
import com.supermarket_simualtor.utils.StringUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@RequiredArgsConstructor
public abstract class AbstractCustomer implements Customer {
    private final CustomRandom random = CustomRandom.getInstance();
    private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    @Getter
    private final String name;

    @Getter
    final int age;

    @Getter
    private final Basket basket = new Basket();

    @Override
    public void visit(SupermarketController supermarket) {
        tryTakeProduct(supermarket);
        payForProducts(supermarket);
    }

    private void payForProducts(SupermarketController supermarket) {
        val desk = random.getRandomFor(supermarket.getDesks());
        logger.info("{} going to desk {}", name, desk.getId());
        desk.serveCustomer(this);
    }

    private void tryTakeProduct(SupermarketController supermarket) {
        for (val item : supermarket.getAssortment()) {
            val taking = random.nextBoolean();
            if (taking) {
                try {
                    val free = basket.freeCapacity();
                    val quantity = supermarket.getQuantityFor(item);
                    if (quantity == 0 || free == 0) {
                        continue;
                    }
                    val operation = new AddToBasketOperation(supermarket, item, free, quantity).invoke();
                    double size = operation.getSize();
                    boolean weighted = operation.isWeighted();
                    logResult(item, size, weighted);
                } catch (ProductNotFoundException | NonWeightedTakeException e) {
                    logger.error(e.getMessage());
                    return;
                }
            }
        }
    }

    private void logResult(String item, double size, boolean weighted) {
        if (weighted && logger.isInfoEnabled()) {
            logger.info("{} put {}g of {} to basket", getName(), StringUtils.friendlyDouble(size), item);
        }
        if (!weighted) {
            logger.info("{} put {} of {} items to basket", getName(), Math.round(size), item);
        }
    }

    private class AddToBasketOperation {
        private static final double THRESHOLD = 0.001;

        private SupermarketController supermarket;
        private String item;
        private int free;
        private long quantity;
        private double size;
        private boolean weighted;

        AddToBasketOperation(SupermarketController supermarket, String item, int free, long quantity) {
            this.supermarket = supermarket;
            this.item = item;
            this.free = free;
            this.quantity = quantity;
        }

        double getSize() {
            return size;
        }

        boolean isWeighted() {
            return weighted;
        }

        AddToBasketOperation invoke() throws ProductNotFoundException, NonWeightedTakeException {
            size = quantity > 1 && free > 1 ? random.getRandomInRange(1, free) : 1;
            weighted = false;
            var product = supermarket.tryTakeProduct(item);
            if (product.isWeighted()) {
                weighted = true;
                val weight = random.getRandomInRange(product.getWeight() / 10, product.getWeight());
                val rest = product.take(weight);
                basket.add(product);
                if (isZero(rest.getWeight())) {
                    supermarket.putProductBack(rest);
                }
                size = weight;
                return this;
            }
            for (int i = 1; i < size; i++) {
                product = supermarket.tryTakeProduct(item);
                basket.add(product);
            }
            return this;
        }

        private boolean isZero(double value) {
            return value >= -THRESHOLD && value <= THRESHOLD;
        }
    }
}
