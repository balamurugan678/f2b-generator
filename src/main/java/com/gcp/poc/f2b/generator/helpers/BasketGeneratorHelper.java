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
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasketGeneratorHelper {

    private BasketGenerator basketGenerator;
    private PubsubHelper pubsubHelper;
    private RiskHelper riskHelper;
    private ThreadLocalRandom random = ThreadLocalRandom.current();

    public BasketGeneratorHelper(PubsubHelper pubsubHelper) throws IOException {
        basketGenerator = new BasketGenerator();
        this.pubsubHelper = pubsubHelper;
        riskHelper = new RiskHelper();
    }

    public void generateInvalidParty(boolean sendToPubsub, boolean printToConsole) throws InterruptedException, TemplateException, ExecutionException, IOException {
        this.generateInvalidParty(sendToPubsub, printToConsole, "PT0001");
    }

    public void generateInvalidParty(boolean sendToPubsub, boolean printToConsole, String partyNameToInvalidate) throws IOException, TemplateException, ExecutionException, InterruptedException {
        LocalDateTime dateTime  = LocalDateTime.now().minusMinutes(10);
        Map<String, Object> data = basketGenerator.nextData(dateTime);
        String xml = basketGenerator.next(data);
        xml = xml.replaceFirst("<partyId partyIDScheme=\"http://www.fpml.org/coding-scheme/LegalEntity\">" + partyNameToInvalidate + "</partyId>", "<partyId partyIDScheme=\"http://www.fpml.org/coding-scheme/LegalEntity\">INVALID_PARTY</partyId>");
        List<String> riskEntries = riskHelper.getBasketEntries(data);

        if (printToConsole) {
            System.out.println(xml);
            for (String riskEntry : riskEntries) {
                System.out.println(riskEntry);
            }
        }

        if (sendToPubsub) {
            ApiFuture<String> messageIdFuture = pubsubHelper.send(xml, "basket", dateTime.toLocalDate());
            System.out.println("Published 1 invalid basket with message ID: " + messageIdFuture.get());
            List<ApiFuture<String>> messageIdFutures = new ArrayList<>();
            for(String riskEntry:riskEntries) {
                messageIdFutures.add(pubsubHelper.send(riskEntry, "risk", dateTime.toLocalDate()));
            }
            List<String> messageIds = ApiFutures.allAsList(messageIdFutures).get();
            System.out.println("Published " + messageIds.size() + " risk message");
        } else  {
            System.out.println("Generated 1 invalid basket and 1 risk message");
        }
    }

    // todo: send risk report entry
    public void generateDuplicatePair(boolean sendToPubsub, boolean printToConsole) throws IOException, TemplateException, ExecutionException, InterruptedException {
        // Generate first message
        LocalDateTime dateTime  = LocalDateTime.now().minusMinutes(5);
        Map<String, Object> data = basketGenerator.nextData(dateTime);
        String xml = basketGenerator.next(data);

        if (printToConsole) {
            System.out.println(xml);
            System.out.println();
        }
        if (sendToPubsub) {
            ApiFuture<String> messageIdFuture = pubsubHelper.send(xml, "basket", dateTime.toLocalDate());
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
        Pattern p2 = Pattern.compile("<trade id=\"(\\d{12})\">");
        Matcher m2 = p2.matcher(xml);
        while (m2.find()) {
            xml = xml.replaceAll(m2.group(1), randomHelper.numberDigits(12)+"");
        }

        // Send duplicate message with Ids replaced
        if (printToConsole) {
            System.out.println(xml);
        }
        if (sendToPubsub) {
            ApiFuture<String> messageIdFuture = pubsubHelper.send(xml, "basket", dateTime.toLocalDate());
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
        int totalBasketMessagesSent = 0;
        int totalRiskMessagesSent = 0;
        int totalAmendmentsSent = 0;
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            LocalTime startTime = LocalTime.of(8, 0);
            LocalTime endTime = LocalTime.of(17, 0);
            int messagesGenerated = 0;
            List<ApiFuture<String>> basketMessageIdFutures = new ArrayList<>();
            List<ApiFuture<String>> riskMessageIdFutures = new ArrayList<>();
            for (LocalTime time = startTime; time.isBefore(endTime) && messagesGenerated < messagesPerDay; time = time.plusNanos(random.nextInt(8, 5000000))) {
                Map<String, Object> data = basketGenerator.nextData(date.atTime(time));
                String xml = basketGenerator.next(data);
                List<String> riskEntries = riskHelper.getBasketEntries(data);

                // Send basket
                ApiFuture<String> messageIdFuture;
                ApiFuture<String> messageIdFuture = pubsubHelper.send(xml,"basket", date);
                basketMessageIdFutures.add(messageIdFuture);

                // Send risk entry
                for (String riskEntry : riskEntries) {
                    messageIdFuture = pubsubHelper.send(riskEntry, "risk", date);
                    riskMessageIdFutures.add(messageIdFuture);
                }

                messagesGenerated++;

                // increment version to simulate amendments
                boolean amendTrade = random.nextInt(0,1000) == 0;
                if (amendTrade) {
                    String xml2 = xml.replace("<sequenceNumber>1</sequenceNumber>","<sequenceNumber>2</sequenceNumber>");
                    ApiFuture<String> messageIdFuture2 = pubsubHelper.send(xml2,"basket", date);
                    basketMessageIdFutures.add(messageIdFuture2);
                    messagesGenerated++;
                    totalAmendmentsSent++;
                }

                // workaround for DEADLINE_EXCEEDED runtime exception
                if (messagesGenerated % 300 == 0) {
                    List<String> acks = ApiFutures.allAsList(basketMessageIdFutures).get();
                    basketMessageIdFutures.clear();
                    totalBasketMessagesSent += acks.size();
                    System.out.print("Sent " + totalBasketMessagesSent + "/" + messages + " baskets in " + (System.nanoTime() - timerStart)/1000000 + "ms");

                    acks = ApiFutures.allAsList(riskMessageIdFutures).get();
                    riskMessageIdFutures.clear();
                    totalRiskMessagesSent += acks.size();
                    System.out.println(" (" + acks.size() + " risks)");
                }
            }

            // Wait for pubsub message acks
            List<String> acks = ApiFutures.allAsList(basketMessageIdFutures).get();
            totalBasketMessagesSent += acks.size();
            System.out.print("Sent " + totalBasketMessagesSent + "/" + messages + " baskets in " + (System.nanoTime() - timerStart)/1000000 + "ms");

            acks = ApiFutures.allAsList(riskMessageIdFutures).get();
            totalRiskMessagesSent += acks.size();
            System.out.println(" (" + acks.size() + " risks)");
        }

        long timerEnd = System.nanoTime();
        System.out.println("Sent total of " + totalBasketMessagesSent + " baskets in " + (timerEnd - timerStart)/1000000 + "ms");
        System.out.println("Sent total of " + totalAmendmentsSent + " amendments");
        System.out.println("Sent total of " + totalRiskMessagesSent + " risks");
    }
}
