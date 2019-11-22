package com.supermarket_simualtor.random;

import lombok.val;

import java.util.List;
import java.util.Random;

public class CustomRandom extends Random {
    private static CustomRandom instance;

    private CustomRandom(long seed) {
        super(seed);
    }

    private CustomRandom() {
        super();
    }

    public static CustomRandom getInstance() {
        if (instance == null) {
            instance = new CustomRandom(System.currentTimeMillis());
        }
        return instance;
    }

    public int getRandomInRange(long min, long max) {
        return getRandomInRange((int) min, (int) max);
    }

    public int getRandomInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        return nextInt((max - min) + 1) + min;
    }

    public double getRandomInRange(double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        return min + (max - min) * nextDouble();
    }

    public <T> T getRandomFor(List<T> objects) {
        val index = getRandomInRange(0, objects.size() - 1);
        return objects.get(index);
    }
}
