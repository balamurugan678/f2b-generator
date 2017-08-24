package com.gcp.poc.f2b.generator.helpers;

import com.gcp.poc.f2b.generator.SwapGenerator;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public class SwapGeneratorHelper {

    private SwapGenerator swapGenerator;
    private PubsubHelper pubsubHelper;
    private ThreadLocalRandom random = ThreadLocalRandom.current();
    private RiskHelper riskHelper;
    private DateHelper dateHelper;

    public SwapGeneratorHelper(PubsubHelper pubsubHelper) throws IOException {
        swapGenerator = new SwapGenerator();
        this.pubsubHelper = pubsubHelper;
        riskHelper = new RiskHelper();
        dateHelper = new DateHelper();
    }

    // Sends a number of messages over the last 5 days
    public void generate(int messages) throws IOException, TemplateException, ExecutionException, InterruptedException {
        // Calculate messages sent per day
        if (messages % 5 != 0) {
            throw new IllegalArgumentException("Messages must be a multiple of 5");
        }
        int messagesPerDay = messages / 5;

        //for each day of last week, generate x trades
        //todo: skip weekend days (and holidays?)
        LocalDate start = dateHelper.minusWorkingDays(LocalDate.now(), 5);
        LocalDate end = LocalDate.now();

        long timerStart = System.nanoTime();
        System.out.println("Start sending swaps");
        int totalSwapMessagesSent = 0;
        int totalRiskMessagesSent = 0;
        int totalAmendmentsSent = 0;
        for (LocalDate date = start; date.isBefore(end); date = dateHelper.addWorkingDays(date, 1)) {
            LocalTime startTime = LocalTime.of(8, 0);
            LocalTime endTime = LocalTime.of(17, 0);
            int messagesGenerated = 0;
            List<ApiFuture<String>> swapMessageIdFutures = new ArrayList<>();
            List<ApiFuture<String>> riskMessageIdFutures = new ArrayList<>();
            for (LocalTime time = startTime; time.isBefore(endTime) && messagesGenerated < messagesPerDay; time = time.plusNanos(random.nextInt(8, 5000000))) {
                Map<String, Object> data = swapGenerator.nextData(date.atTime(time));
                String xml = swapGenerator.next(data);
                //String riskEntry = riskHelper.getSwapEntry(data);

                // Send swap trade
                ApiFuture<String> messageIdFuture = pubsubHelper.send(xml, "swap", date);
                swapMessageIdFutures.add(messageIdFuture);

                // Send risk entry
                //messageIdFuture = pubsubHelper.send(riskEntry, "risk", date);
                //riskMessageIdFutures.add(messageIdFuture);

                messagesGenerated++;

                // increment version to simulate amendments
                boolean amendTrade = random.nextInt(0,1000) == 0;
                if (amendTrade) {
                    String xml2 = xml.replace("<sequenceNumber>1</sequenceNumber>","<sequenceNumber>2</sequenceNumber>");
                    ApiFuture<String> messageIdFuture2 = pubsubHelper.send(xml2,"basket", date);
                    swapMessageIdFutures.add(messageIdFuture2);
                    messagesGenerated++;
                    totalAmendmentsSent++;

                    // Send risk entry (with incremented version)
                    //String riskEntry2 = riskEntry.replace("\"tradeVersion\": \"1\"","\"tradeVersion\": \"2\"");
                    //messageIdFuture = pubsubHelper.send(riskEntry2, "risk", date);
                    //riskMessageIdFutures.add(messageIdFuture);
                }


                // workaround for DEADLINE_EXCEEDED runtime exception
                if (messagesGenerated % 50 == 0) {
                    List<String> acks = ApiFutures.allAsList(swapMessageIdFutures).get();
                    swapMessageIdFutures.clear();
                    totalSwapMessagesSent += acks.size();
                    System.out.print("Sent " + totalSwapMessagesSent + "/" + messages + " swaps in " + (System.nanoTime() - timerStart)/1000000 + "ms");

                    acks = ApiFutures.allAsList(riskMessageIdFutures).get();
                    riskMessageIdFutures.clear();
                    totalRiskMessagesSent += acks.size();
                    System.out.println(" (" + acks.size() + " risks)");
                }
            }

            // Wait for pubsub message acks
            List<String> acks = ApiFutures.allAsList(swapMessageIdFutures).get();
            totalSwapMessagesSent += acks.size();
            System.out.print("Sent " + totalSwapMessagesSent + "/" + messages + " swaps in " + (System.nanoTime() - timerStart)/1000000 + "ms");

            acks = ApiFutures.allAsList(riskMessageIdFutures).get();
            totalRiskMessagesSent += acks.size();
            System.out.println(" (" + acks.size() + " risks)");
        }

        long timerEnd = System.nanoTime();
        System.out.println("Sent total of " + totalSwapMessagesSent + " swaps in " + (timerEnd - timerStart)/1000000 + "ms");
        System.out.println("Sent total of " + totalAmendmentsSent + " amendments");
        System.out.println("Sent total of " + totalRiskMessagesSent + " risks");
    }
}
