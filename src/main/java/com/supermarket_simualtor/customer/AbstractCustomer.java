package com.supermarket_simualtor.customer;

import com.supermarket_simualtor.basket.Basket;
import com.supermarket_simualtor.random.CustomRandom;
import com.supermarket_simualtor.supermarket.ProductNotFoundException;
import com.supermarket_simualtor.supermarket.SupermarketController;
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
                    val size = quantity > 1 && free > 1 ? random.getRandomInRange(1, free) : 1;
                    for (int i = 0; i < size; i++) {
                        val product = supermarket.tryTakeProduct(item);
                        basket.add(product);
                    }
                    logger.info("{} put {} of {} items to basket", getName(), size, item);
                } catch (ProductNotFoundException e) {
                    logger.error(e.getMessage());
                    return;
                }
            }
        }
    }
}
