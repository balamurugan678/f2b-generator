package com.gcp.poc.f2b.generator.helpers;

import com.gcp.poc.f2b.generator.BasketGenerator;
import com.gcp.poc.f2b.generator.SwapGenerator;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public class SwapGeneratorHelper {

    private SwapGenerator swapGenerator;
    private PubsubHelper pubsubHelper;
    private ThreadLocalRandom random = ThreadLocalRandom.current();

    public SwapGeneratorHelper(PubsubHelper pubsubHelper) throws IOException {
        swapGenerator = new SwapGenerator();
        this.pubsubHelper = pubsubHelper;
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
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now();

        long timerStart = System.nanoTime();
        System.out.println("Start sending swaps");
        int totalMessagesSent = 0;
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            LocalTime startTime = LocalTime.of(8, 0);
            LocalTime endTime = LocalTime.of(17, 0);
            int messagesGenerated = 0;
            List<ApiFuture<String>> messageIdFutures = new ArrayList<>();
            for (LocalTime time = startTime; time.isBefore(endTime) && messagesGenerated < messagesPerDay; time = time.plusNanos(random.nextInt(8, 1000000))) {
                String xml = swapGenerator.next(date.atTime(time));

                ApiFuture<String> messageIdFuture = pubsubHelper.send(xml, "swap", date);
                messageIdFutures.add(messageIdFuture);

                messagesGenerated++;

                // workaround for DEADLINE_EXCEEDED runtime exception
                if (messagesGenerated % 50 == 0) {
                    List<String> acks = ApiFutures.allAsList(messageIdFutures).get();
                    messageIdFutures.clear();
                    totalMessagesSent += acks.size();
                    System.out.println("Sent " + totalMessagesSent + "/" + messages + " swaps in " + (System.nanoTime() - timerStart)/1000000 + "ms");
                }
            }

            // Wait for pubsub message acks
            List<String> acks = ApiFutures.allAsList(messageIdFutures).get();
            totalMessagesSent += acks.size();
            System.out.println("Sent " + totalMessagesSent + "/" + messages + " swaps in " + (System.nanoTime() - timerStart)/1000000 + "ms");
        }

        long timerEnd = System.nanoTime();
        System.out.println("Sent total of " + totalMessagesSent + " swaps in " + (timerEnd - timerStart)/1000000 + "ms");
    }
}
