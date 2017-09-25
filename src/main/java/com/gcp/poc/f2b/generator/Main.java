package com.gcp.poc.f2b.generator;

import com.gcp.poc.f2b.generator.controllers.SwapController;
import freemarker.template.TemplateException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
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

        SpringApplication.run(Main.class);

        /*PubsubHelper pubsubHelper = new PubsubHelper();
        BigTableHelper bigTableHelper = new BigTableHelper();

        BasketGeneratorHelper basketGeneratorHelper = new BasketGeneratorHelper(pubsubHelper, bigTableHelper);
        basketGeneratorHelper.generate(100);

        SwapGeneratorHelper swapGeneratorHelper = new SwapGeneratorHelper(pubsubHelper, bigTableHelper);
        swapGeneratorHelper.generate(100);*/
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