package com.gcp.poc.f2b.generator.model;

public class Trade {
    private long tradeId;
    private Book book;
    private Party party;
    private ExchangeRate exchangeRate;
    private long amount1;
    private long amount2;

    public long getTradeId() {
        return tradeId;
    }

    public void setTradeId(long tradeId) {
        this.tradeId = tradeId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public ExchangeRate getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(ExchangeRate exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public long getAmount1() {
        return amount1;
    }

    public void setAmount1(long amount1) {
        this.amount1 = amount1;
    }

    public long getAmount2() {
        return amount2;
    }

    public void setAmount2(long amount2) {
        this.amount2 = amount2;
    }
}
