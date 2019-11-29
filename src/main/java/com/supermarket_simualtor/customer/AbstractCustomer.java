package com.supermarket_simualtor.customer;

import com.supermarket_simualtor.basket.Basket;
import com.supermarket_simualtor.bill.Bill;
import com.supermarket_simualtor.customer.payment.*;
import com.supermarket_simualtor.customer.storage.Bonuses;
import com.supermarket_simualtor.customer.storage.Card;
import com.supermarket_simualtor.customer.storage.MoneyStorage;
import com.supermarket_simualtor.customer.storage.Wallet;
import com.supermarket_simualtor.product.NonWeightedTakeException;
import com.supermarket_simualtor.random.CustomRandom;
import com.supermarket_simualtor.supermarket.ProductNotFoundException;
import com.supermarket_simualtor.supermarket.SupermarketController;
import com.supermarket_simualtor.utils.StringUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.Nullable;
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
    private final Card card;
    private final Bonuses bonuses;

    @Getter
    private final Basket basket = new Basket();

    @Override
    public void visit(SupermarketController supermarket) {
        tryTakeProduct(supermarket);
        payForProducts(supermarket);
    }

    @Override
    public boolean payForBill(Bill bill) {
        val total = bill.getTotal();
        val method = getPaymentMethod(total);
        if (method == null) {
            if (logger.isInfoEnabled()) {
                logger.info("{} has not enough money to pay {}$", getName(), StringUtils.friendlyDouble(total));
            }
            return false;
        }
        Payment payment = createPayment(method);
        payment.pay(total);
        bonuses.add(bill.getBonuses());
        if (logger.isInfoEnabled()) {
            logger.info("{} got {} bonuses", getName(), StringUtils.friendlyDouble(bill.getBonuses()));
        }
        if (logger.isInfoEnabled()) {
            logger.info(
                "{} has total {}$ and {} bonuses",
                getName(),
                StringUtils.friendlyDouble(wallet.get() + card.get()),
                StringUtils.friendlyDouble(bonuses.get())
            );
        }
        return true;
    }

    @Nullable
    private PaymentMethod getPaymentMethod(double total) {
        PaymentMethod method = null;
        if (hasEnoughMoney(wallet, total)) {
            method = PaymentMethod.CASH;
        } else if (hasEnoughMoney(card, total)) {
            method = PaymentMethod.CARD;
        } else if (hasEnoughMoney(bonuses, total)) {
            method = PaymentMethod.BONUS;
        }
        return method;
    }

    private Payment createPayment(PaymentMethod method) {
        Payment payment = null;
        switch (method) {
            case CARD:
                payment = new CardPayment(card);
                break;
            case CASH:
                payment = new CashPayment(wallet);
                break;
            case BONUS:
                payment = new BonusPayment(bonuses);
                break;
            default:
                break;
        }
        return payment;
    }

    private void payForProducts(SupermarketController supermarket) {
        val desk = random.getRandomFor(supermarket.getDesks());
        logger.info("{} going to desk {}", name, desk.getId());
        desk.serveCustomer(this, supermarket);

    }

    private void tryTakeProduct(SupermarketController supermarket) {
        for (val product : supermarket.getAssortment()) {
            val taking = random.nextBoolean();
            if (taking) {
                try {
                    val free = basket.getFreeCapacity();
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

    private boolean hasEnoughMoney(MoneyStorage storage, double money) {
        return storage.get() >= money;
    }

    private class AddToBasketOperation {
        private static final double EPS = 10; // 10g
        private static final double MIN_WEIGHTED_PER_CUSTOMER = 100; // 100g
        private static final double MAX_WEIGHTED_PER_CUSTOMER = 1000; // 1kg

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
            if (product.isWeighted()) {
                weighted = true;
                var weight = random.getRandomInRange(
                    Math.min(product.getWeight() / EPS, MIN_WEIGHTED_PER_CUSTOMER),
                    Math.min(product.getWeight(), MAX_WEIGHTED_PER_CUSTOMER)
                );
                val rest = product.take(weight);
                basket.add(product);
                if (!isZero(rest.getWeight())) {
                    supermarket.putProductBack(rest);
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Throwing {}g of {} to trash", StringUtils.friendlyDouble(rest.getWeight()), item);
                    }
                }
                size = weight;
                return this;
            }
            size = quantity > 1 && free > 1 ? random.getRandomInRange(1, free) : 1;
            for (int i = 1; i < size; i++) {
                product = supermarket.tryTakeProduct(item);
                basket.add(product);
            }
            return this;
        }

        private boolean isZero(double value) {
            return value >= -EPS && value <= EPS;
        }
    }
}
