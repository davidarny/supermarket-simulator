package com.supermarket_simualtor.customer;

import com.supermarket_simualtor.basket.Basket;
import com.supermarket_simualtor.bill.Bill;
import com.supermarket_simualtor.customer.wallet.Wallet;
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

    private final Wallet wallet;

    @Getter
    private final Basket basket = new Basket();

    @Override
    public void visit(SupermarketController supermarket) {
        tryTakeProduct(supermarket);
        payForProducts(supermarket);
    }

    @Override
    public double payForBill(Bill bill) {
        val total = bill.getTotal();
        val payingWithBonuses = random.nextBoolean();
        if (payingWithBonuses) {
            wallet.payWithCashAndBonuses(total);
        } else {
            wallet.payWithCash(total);
        }
        double bonuses = bill.getBonuses();
        if (logger.isInfoEnabled()) {
            logger.info("{} got {} bonuses", getName(), StringUtils.friendlyDouble(bonuses));
        }
        wallet.addBonuses(bonuses);
        double rest = wallet.getTotal();
        if (logger.isInfoEnabled()) {
            logger.info(
                "{} has total {}$ and {} bonuses in wallet",
                getName(),
                StringUtils.friendlyDouble(rest),
                StringUtils.friendlyDouble(wallet.getBonuses())
            );
        }
        return rest;
    }

    private void payForProducts(SupermarketController supermarket) {
        val desk = random.getRandomFor(supermarket.getDesks());
        logger.info("{} going to desk {}", name, desk.getId());
        desk.serveCustomer(this);

    }

    private void tryTakeProduct(SupermarketController supermarket) {
        for (val product : supermarket.getAssortment()) {
            val taking = random.nextBoolean();
            if (taking) {
                try {
                    val free = basket.freeCapacity();
                    var quantity = supermarket.getQuantityFor(product);
                    if (quantity == 0 || free == 0) {
                        continue;
                    }
                    val operation = new AddToBasketOperation(supermarket, product, free, quantity).invoke();
                    double size = operation.getSize();
                    boolean weighted = operation.isWeighted();
                    logResult(product, size, weighted);
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
            weighted = false;
            var product = supermarket.tryTakeProduct(item);
            val price = supermarket.getPricing().get(product.getName());
            if (product.isWeighted()) {
                weighted = true;
                var weight = random.getRandomInRange(product.getWeight() / 10, product.getWeight());
                val cost = price * (weight / 1000); // g to kg
                if (!wallet.hasEnoughCash(cost)) {
                    assert cost > wallet.getTotal();
                    val diff = cost - wallet.getTotal();
                    val subtract = diff / price;
                    assert subtract > 0;
                    weight -= subtract;
                }
                val rest = product.take(weight);
                basket.add(product);
                if (isZero(rest.getWeight())) {
                    supermarket.putProductBack(rest);
                }
                size = weight;
                return this;
            }
            size = quantity > 1 && free > 1 ? random.getRandomInRange(1, free) : 1;
            val cost = price * size;
            if (!wallet.hasEnoughCash(cost)) {
                assert cost > wallet.getTotal();
                val diff = cost - wallet.getTotal();
                val subtract = Math.round(diff / price);
                assert subtract > 0;
                size -= subtract;
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
