package com.gcp.poc.f2b.generator.helpers;

import com.google.api.core.ApiFuture;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.pubsub.v1.PublisherGrpc;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class PubsubHelper {

    //private TopicName topicName = TopicName.create("gcp-f2brb", "f2b_fx_trades_topic");
    private TopicName topicName = TopicName.create("gcp-f2brb", "f2b_store_basket_topic");
    private Publisher publisher;

    public PubsubHelper() {

    }

    public void send(String message, String messageType) throws IOException, ExecutionException, InterruptedException {

        // Create a publisher instance with default settings bound to the topic
        publisher = Publisher.defaultBuilder(topicName).build();

        ByteString data = ByteString.copyFromUtf8(message);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().putAttributes("messageType",messageType).setData(data).build();
        ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);

        System.out.println("published with message ID: " + messageIdFuture.get());
    }
}
