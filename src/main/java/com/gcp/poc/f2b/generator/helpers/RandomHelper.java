package com.gcp.poc.f2b.generator.helpers;

import com.gcp.poc.f2b.generator.model.Book;

import java.util.concurrent.ThreadLocalRandom;

public class RandomHelper {

    private final ThreadLocalRandom random;
    private final Book book;

    public RandomHelper(ThreadLocalRandom random, Book book) {
        this.random = random;
        this.book = book;
    }

    public String tradeMsgId() {
        return "GCPFX" + book.getTradingRegion() + random.nextLong(10000000000000L, 99999999999999L);
    }

    public long numberDigits(int digits) {
        long min = Math.round(Math.pow(10,digits));
        long max = min * 10 - 1;
        return random.nextLong(min, max);
    }

    public long numberRangeMax2sf(long low, long high) {
        long temp = (low/10);
        return random.nextLong(low, high)/temp*temp;
    }
}
