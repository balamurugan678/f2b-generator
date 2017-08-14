package com.gcp.poc.f2b.generator.helpers;

//import com.solacesystems.jcsmp.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class SolaceHelper {
//
//    private final String host;
//    private final String vpnName;
//    private final String username;
//    private final String password;
//
//    private JCSMPSession session;
//    private XMLMessageProducer producer;
//    private Topic topic;
//
//    public SolaceHelper(String host, String vpnName, String username, String password) {
//        this.host = host;
//        this.vpnName = vpnName;
//        this.username = username;
//        this.password = password;
//    }
//
//    public void init() throws JCSMPException {
//        connect();
//        producer = createProducer();
//        topic = JCSMPFactory.onlyInstance().createTopic("f2b/trade");
//    }
//
//    private void connect() throws JCSMPException {
//        JCSMPProperties properties = new JCSMPProperties();
//        properties.setProperty(JCSMPProperties.HOST, host);
//        properties.setProperty(JCSMPProperties.VPN_NAME, vpnName);
//        properties.setProperty(JCSMPProperties.USERNAME, username);
//        properties.setProperty(JCSMPProperties.PASSWORD, password);
//        session = JCSMPFactory.onlyInstance().createSession(properties);
//        session.connect();
//        System.out.println("Connected to solace.");
//    }
//
//    private XMLMessageProducer createProducer() throws JCSMPException {
//        return session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
//            @Override
//            public void handleError(String s, JCSMPException e, long l) {
//                System.out.println(String.format("error connecting to solace: {} error code: {}", s, l, e));
//            }
//
//            @Override
//            public void responseReceived(String s) {
//                System.out.println(String.format("response received from solace: {}", s));
//            }
//        });
//    }
//
//    public XMLMessage createMessage() throws JCSMPException {
//        return producer.createBytesXMLMessage();
//    }
//
//    public void sendMessage(XMLMessage message) throws JCSMPException {
//        producer.send(message, topic);
//    }

}