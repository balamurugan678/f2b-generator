package com.gcp.poc.f2b.generator.helpers;

import com.gcp.poc.f2b.generator.model.Book;

import java.util.concurrent.ThreadLocalRandom;

public class RandomHelper {

    private final ThreadLocalRandom random;

    public RandomHelper(ThreadLocalRandom random) {
        this.random = random;
    }

    // todo: there is a bug - it generates digits+1 number of digits
    public long numberDigits(int digits) {
        if (digits < 2) {
            throw new IllegalArgumentException("Digits must be > 1");
        }
        long min = Math.round(Math.pow(10,digits-1));
        long max = min * 10 - 1;
        return random.nextLong(min, max);
    }

    public long numberRangeMax2sf(long low, long high) {
        long temp = (low/10);
        return random.nextLong(low, high)/temp*temp;
    }

    public long number(long min, long max) {
        return random.nextLong(min, max);
    }
}
