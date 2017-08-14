package com.gcp.poc.f2b.generator.helpers;

import com.gcp.poc.f2b.generator.BasketGenerator;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasketGeneratorHelper {

    private BasketGenerator basketGenerator;
    private PubsubHelper pubsubHelper;
    private ThreadLocalRandom random = ThreadLocalRandom.current();

    public BasketGeneratorHelper(PubsubHelper pubsubHelper) throws IOException {
        basketGenerator = new BasketGenerator();
        this.pubsubHelper = pubsubHelper;
    }

    public void generateInvalidParty() throws InterruptedException, TemplateException, ExecutionException, IOException {
        this.generateInvalidParty(true, false);
    }

    public void generateInvalidParty(boolean sendToPubsub, boolean printToConsole) throws InterruptedException, TemplateException, ExecutionException, IOException {
        this.generateInvalidParty(true, false, "PT0001");
    }

    public void generateInvalidParty(boolean sendToPubsub, boolean printToConsole, String partyNameToInvalidate) throws IOException, TemplateException, ExecutionException, InterruptedException {
        String xml = basketGenerator.next(LocalDateTime.now().minusMinutes(10));
        xml = xml.replaceFirst("<partyId partyIDScheme=\"http://www.fpml.org/coding-scheme/LegalEntity\">" + partyNameToInvalidate + "</partyId>", "<partyId partyIDScheme=\"http://www.fpml.org/coding-scheme/LegalEntity\">INVALID_PARTY</partyId>");

        if (printToConsole) {
            System.out.println(xml);
        }

        if (sendToPubsub) {
            ApiFuture<String> messageIdFuture = pubsubHelper.send(xml, "basket");
            System.out.println("Published 1 invalid basket with message ID: " + messageIdFuture.get());
        } else  {
            System.out.println("Generated 1 invalid basket");
        }
    }

    public void generateDuplicatePair(boolean sendToPubsub, boolean printToConsole) throws IOException, TemplateException, ExecutionException, InterruptedException {
        // Generate first message
        String xml = basketGenerator.next(LocalDateTime.now().minusMinutes(5));

        if (printToConsole) {
            System.out.println(xml);
            System.out.println();
        }
        if (sendToPubsub) {
            ApiFuture<String> messageIdFuture = pubsubHelper.send(xml, "basket");
            messageIdFuture.get();
        }

        // Replace correlationId
        Pattern p = Pattern.compile(">(.*?)</correlationId>");
        Matcher m = p.matcher(xml);
        m.find();
        String tradeId = m.group(1);
        RandomHelper randomHelper = new RandomHelper(random);
        xml = xml.replace(tradeId, randomHelper.numberDigits(12)+"");
        //replace tradeIds
        Pattern p2 = Pattern.compile("<trade id=\"(\\d12)\">");
        Matcher m2 = p2.matcher(xml);
        while (m2.find()) {
            xml.replaceAll(m.group(1), randomHelper.numberDigits(12)+"");
        }

        // Send duplicate message with Ids replaced
        if (printToConsole) {
            System.out.println(xml);
        }
        if (sendToPubsub) {
            ApiFuture<String> messageIdFuture = pubsubHelper.send(xml, "basket");
            messageIdFuture.get();
        }

        System.out.println("Generated 2 invalid baskets");
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
        System.out.println("Start sending baskets");
        int totalMessagesSent = 0;
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            LocalTime startTime = LocalTime.of(8, 0);
            LocalTime endTime = LocalTime.of(17, 0);
            int messagesGenerated = 0;
            List<ApiFuture<String>> messageIdFutures = new ArrayList<>();
            for (LocalTime time = startTime; time.isBefore(endTime) && messagesGenerated < messagesPerDay; time = time.plusMinutes(0 + random.nextInt(3)).plusNanos(random.nextInt(8, 1000000))) {
                String xml = basketGenerator.next(date.atTime(time));

                ApiFuture<String> messageIdFuture = pubsubHelper.send(xml,"basket");
                messageIdFutures.add(messageIdFuture);

                messagesGenerated++;
            }

            // Wait for pubsub message acks
            List<String> acks = ApiFutures.allAsList(messageIdFutures).get();
            totalMessagesSent += acks.size();
            System.out.println("Sent " + totalMessagesSent + "/" + messages + " baskets in " + (System.nanoTime() - timerStart)/1000000 + "ms");
        }

        long timerEnd = System.nanoTime();
        System.out.println("Sent total of " + totalMessagesSent + " baskets in " + (timerEnd - timerStart)/1000000 + "ms");
    }
}
