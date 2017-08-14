package com.gcp.poc.f2b.generator.helpers;

import com.google.api.core.ApiFuture;
import com.google.api.gax.batching.BatchingSettings;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.pubsub.v1.PublisherGrpc;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.threeten.bp.Duration;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class PubsubHelper {

    //private TopicName topicName = TopicName.create("gcp-f2brb", "f2b_fx_trades_topic");
    //private TopicName topicName = TopicName.create("gcp-f2brb", "f2b_store_basket_topic");
    private TopicName topicName = TopicName.create("gcp-f2brb", "f2b_inbound_trade_topic");
    private Publisher publisher;

    public PubsubHelper() throws IOException {
        // Settings for publisher
        BatchingSettings batchingSettings = BatchingSettings.newBuilder()
                .setDelayThreshold(Duration.ofMillis(100))
                .setRequestByteThreshold(10000L)
                .setElementCountThreshold(100L)
                .build();

        // Create a publisher instance with default settings bound to the topic
        publisher = Publisher.defaultBuilder(topicName)
                .setBatchingSettings(batchingSettings)
                .build();
    }

    public ApiFuture<String> send(String message, String messageType) throws IOException, ExecutionException, InterruptedException {
        ByteString data = ByteString.copyFromUtf8(message);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().putAttributes("messageType",messageType).setData(data).build();
        ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);

        return messageIdFuture;
    }
}
