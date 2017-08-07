package com.gcp.poc.f2b.generator;


import sun.plugin2.message.Message;

import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

public class MessageGenerator {

    private ThreadLocalRandom random;

    private int[][] typeSelectors;    //0=template, 1=book, 2=party, 3=formula  note: formulas are predefined.
    private String[][][] data;


    private int bookIndex;
    private int[] bookSelector;
    private String[][] books;

    private static final String TRADE_PREFIX = "X";

    public MessageGenerator() {
        random = ThreadLocalRandom.current();

        // temp init data
        typeSelectors = new int[][]{{0, 0}, {0, 1}};


    }

    public String nextMessage() {

        StringBuilder sb = new StringBuilder();
        int book = random.nextInt(15);
        int party = random.nextInt(10);

        for (int i = 0; i<typeSelectors.length; i++) {
            int type = typeSelectors[i][0];
            int subType = typeSelectors[i][1];
            switch(type) {
                case 0:
                    sb.append(data[0][0][subType]);
                    break;
                case 1:
                    // book
                    sb.append(data[type][book][subType]);
                    break;
                case 2:
                    // party
                    sb.append(data[type][party][subType]);
                    break;
                default:
                    // formula
                    switch (subType) {
                        case 0:
                            // currency
                            sb.append(random.nextLong(100000,500000));
                            break;
                        case 2:
                            sb.append(TRADE_PREFIX).append(random.nextInt(30,80));
                            break;
                    }
                    break;
            }

            int bookType = random.nextInt(15);
            int partyType = random.nextInt(15);


        }

        return sb.toString();
    }

    /*
    private static final int ONE_HUNDREAD_MEGABYTES = 100 * 1024 * 1024;

    private final long size;

    private long index;

    private byte[] bytes;

    public MessageGenerator(long size) {
        super();

        if (size < 1) {
            throw new IllegalArgumentException("Block size must be at least one byte!");
        }

        this.size = size;
        this.index = 0;

        bytes = "This is a test. ".getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public int read() throws IOException {
        if (index == size) {
            return -1;
        }

        if (index % ONE_HUNDREAD_MEGABYTES == 0 && index > 0) {
            System.out.println(DateTime.now() + " streamed " + index/ONE_HUNDREAD_MEGABYTES + "00MB");
        }

        return bytes[(int)(index++ % bytes.length)];
    }*/

    /*





     */
}