package com.gcp.poc.f2b.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.xml.XmlMapper;
import com.gcp.poc.f2b.generator.helpers.PubsubHelper;
import com.gcp.poc.f2b.generator.helpers.RandomHelper;
import com.gcp.poc.f2b.generator.helpers.SolaceHelper;
import com.solacesystems.jcsmp.*;
import com.solacesystems.jcsmp.impl.sdt.MapImpl;
import freemarker.template.TemplateException;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
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

    public static void main( String[] args ) throws JCSMPException, IOException, TemplateException, ParseException, ExecutionException, InterruptedException {

        // Create solace helper
        //SolaceHelper solaceHelper = new SolaceHelper(SOLACE_HOST, SOLACE_VPN_NAME, SOLACE_USERNAME, SOLACE_PASSWORD);
        //solaceHelper.init();
        PubsubHelper pubsubHelper = new PubsubHelper();

        // Create generator
        Generator fxGenerator = new Generator("fx");

        //for each day of last week, generate x trades
        ThreadLocalRandom random = ThreadLocalRandom.current();
        //todo: skip weekend days (and holidays?)
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now();

        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            LocalTime startTime = LocalTime.of(07, 30);
            LocalTime endTime = LocalTime.of(05, 30);
            int messagesGenerated = 0;
            for (LocalTime time = startTime; time.isBefore(endTime) || messagesGenerated < 5; time = time.plusMinutes(8 + random.nextInt(4)).plusNanos(random.nextInt(8, 1000000))) {
                String xml = fxGenerator.next(date.atTime(time));

                System.out.println(xml);


                //sendMessage(xml, solaceHelper, sequenceNumber);
                //pubsubHelper.send(xml);

                messagesGenerated++;
            }
        }


        //Generate one invalid message
        String xml = fxGenerator.next(LocalDateTime.now().minusMinutes(10));
        //xml = xml.replaceFirst("<partyId partyIDScheme=\"http://www.fpml.org/coding-scheme/LegalEntity\">.*?</partyId>", "<partyId partyIDScheme=\"http://www.fpml.org/coding-scheme/LegalEntity\">INVALID_PARTY</partyId>");
        xml = xml.replaceFirst("<partyId partyIDScheme=\"http://www.fpml.org/coding-scheme/LegalEntity\">PT0001</partyId>", "<partyId partyIDScheme=\"http://www.fpml.org/coding-scheme/LegalEntity\">INVALID_PARTY</partyId>");
        System.out.println(xml);
        //pubsubHelper.send(xml);

        //Generate a pair of duplicate messages
        xml = fxGenerator.next(LocalDateTime.now().minusMinutes(5));
        System.out.println(xml);
        //pubsubHelper.send(xml);
        //replace correlationId
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
        System.out.println(xml);
        //pubsubHelper.send(xml);


        start = LocalDate.now().minusYears(1);
        end = start.plusDays(100);
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(10)) {

        }

    }

    private static void sendMessage(String payload, SolaceHelper solaceHelper, int seqNum) throws JCSMPException {
        TextMessage message = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
        message.setDeliveryMode(DeliveryMode.PERSISTENT);
        message.setText(payload);

        SDTMap messageProperties = new MapImpl();
        String uuid = UUID.randomUUID().toString();
        messageProperties.putString("runid", ""+seqNum);
        messageProperties.putString("seqnum", String.valueOf(seqNum));
        messageProperties.putString("uuid", UUID.randomUUID().toString());
        messageProperties.putString("datatype", "message");
        messageProperties.putLong("genesisstart", System.nanoTime());
        //messageProperties.putString("datakey", tradeId);
        messageProperties.putString("datakeyname", "tradeid");
        messageProperties.putString("dataversion", "1.0");
        Date now = new Date();
        messageProperties.putString("timestamp", String.valueOf(now.getTime()));
        message.setProperties(messageProperties);

        solaceHelper.sendMessage(message);
    }

    private static String xmlToJson(String xml) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        JsonNode node = xmlMapper.readTree(xml.getBytes());
        ObjectMapper jsonMapper = new ObjectMapper();
        String json = jsonMapper.writeValueAsString(node);
        return json;
    }
}