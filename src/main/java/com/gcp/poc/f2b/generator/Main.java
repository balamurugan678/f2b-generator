package com.gcp.poc.f2b.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.xml.XmlMapper;
import com.gcp.poc.f2b.generator.helpers.BasketGeneratorHelper;
import com.gcp.poc.f2b.generator.helpers.PubsubHelper;
import com.gcp.poc.f2b.generator.helpers.RandomHelper;
import com.gcp.poc.f2b.generator.helpers.SolaceHelper;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import freemarker.template.TemplateException;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final String SOLACE_HOST = "35.187.90.140:55555";
    private static final String SOLACE_VPN_NAME = "genesis"; //or "default"
    private static final String SOLACE_USERNAME = "apollo";
    private static final String SOLACE_PASSWORD = "zerosugar";
    private static final String SOLACE_TOPIC = "apollo/notify";

    public static void main( String[] args ) throws /*JCSMPException,*/ IOException, TemplateException, ParseException, ExecutionException, InterruptedException {

        // Create solace helper
        //SolaceHelper solaceHelper = new SolaceHelper(SOLACE_HOST, SOLACE_VPN_NAME, SOLACE_USERNAME, SOLACE_PASSWORD);
        //solaceHelper.init();

        PubsubHelper pubsubHelper = new PubsubHelper();

        BasketGeneratorHelper basketGeneratorHelper = new BasketGeneratorHelper(pubsubHelper);
        basketGeneratorHelper.generate(100);


        //generateSwap(pubsubHelper, true);
    }

    private static void generateSwap(PubsubHelper pubsubHelper, boolean sendToPubSub) throws IOException, TemplateException, ExecutionException, InterruptedException {
        SwapGenerator swapGenerator = new SwapGenerator("rates");

        List<ApiFuture<String>>  messageIdFutures = new ArrayList<>();

        //for each day of last week, generate x trades
        ThreadLocalRandom random = ThreadLocalRandom.current();
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
            for (LocalTime time = startTime; time.isBefore(endTime) && messagesGenerated < 50; time = time.plusMinutes(0 + random.nextInt(3)).plusNanos(random.nextInt(8, 1000000))) {

                String xml = swapGenerator.next(date.atTime(time));
                //System.out.println(xml);

                if (sendToPubSub) {
                    ApiFuture<String> messageIdFuture = pubsubHelper.send(xml, "swap");
                    messageIdFutures.add(messageIdFuture);
                }

                messagesGenerated++;
                totalMessagesSent++;
            }
        }

        // Wait for pubsub message acks
        ApiFutures.allAsList(messageIdFutures).get();

        long timerEnd = System.nanoTime();
        System.out.println("Sent: " + totalMessagesSent + " swaps in " + (timerEnd - timerStart)/1000000 + "ms");
    }

//    private static void sendMessage(String payload, SolaceHelper solaceHelper, int seqNum) throws JCSMPException {
//        TextMessage message = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
//        message.setDeliveryMode(DeliveryMode.PERSISTENT);
//        message.setText(payload);
//
//        SDTMap messageProperties = new MapImpl();
//        String uuid = UUID.randomUUID().toString();
//        messageProperties.putString("runid", ""+seqNum);
//        messageProperties.putString("seqnum", String.valueOf(seqNum));
//        messageProperties.putString("uuid", UUID.randomUUID().toString());
//        messageProperties.putString("datatype", "message");
//        messageProperties.putLong("genesisstart", System.nanoTime());
//        //messageProperties.putString("datakey", tradeId);
//        messageProperties.putString("datakeyname", "tradeid");
//        messageProperties.putString("dataversion", "1.0");
//        Date now = new Date();
//        messageProperties.putString("timestamp", String.valueOf(now.getTime()));
//        message.setProperties(messageProperties);
//
//        solaceHelper.sendMessage(message);
//    }

    private static String xmlToJson(String xml) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        JsonNode node = xmlMapper.readTree(xml.getBytes());
        ObjectMapper jsonMapper = new ObjectMapper();
        String json = jsonMapper.writeValueAsString(node);
        return json;
    }
}