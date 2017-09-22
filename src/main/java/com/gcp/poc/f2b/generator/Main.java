package com.gcp.poc.f2b.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.xml.XmlMapper;
import com.gcp.poc.f2b.generator.helpers.*;
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
        BigTableHelper bigTableHelper = new BigTableHelper();

        BasketGeneratorHelper basketGeneratorHelper = new BasketGeneratorHelper(pubsubHelper, bigTableHelper);
        basketGeneratorHelper.generate(100);

        SwapGeneratorHelper swapGeneratorHelper = new SwapGeneratorHelper(pubsubHelper, bigTableHelper);
        swapGeneratorHelper.generate(100);
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
}