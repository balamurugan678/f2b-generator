package com.gcp.poc.f2b.generator.helpers;

import com.google.api.core.ApiFuture;
import com.google.api.gax.batching.BatchingSettings;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.springframework.stereotype.Component;
import org.threeten.bp.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class PubsubHelper {

    private TopicName topicName = TopicName.create("gcp-f2brb", "f2b_demo_inlet_topic1");
    private Publisher publisher;

    public PubsubHelper() throws IOException {
        // Settings for publisher
        BatchingSettings batchingSettings = BatchingSettings.newBuilder()
                .setDelayThreshold(Duration.ofMillis(10))
                .setRequestByteThreshold(1000L)
                .setElementCountThreshold(100L)
                .build();

        // Create a publisher instance with default settings bound to the topic
        publisher = Publisher.defaultBuilder(topicName)
                .setBatchingSettings(batchingSettings)
                .build();
    }

    /*
    public ApiFuture<String> send(String message, String messageType, LocalDate tradeDate) throws IOException, ExecutionException, InterruptedException {
        ByteString data = ByteString.copyFromUtf8(message);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .putAttributes("messageType",messageType)
                .putAttributes("creationDate",tradeDate.toString().replaceAll("-",""))
                .setData(data)
                .build();
        ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);

        return messageIdFuture;
    }*/

    public ApiFuture<String> send(String uuid, String messageType, String messageFormat, LocalDate date) {
        ByteString data = ByteString.copyFromUtf8(uuid);
        Map<String, String> attributes = new HashMap();
        attributes.put("uuid",uuid);
        attributes.put("messageType",messageType);
        attributes.put("messageFormat",messageFormat);
        attributes.put("creationDate",date.toString().replaceAll("-",""));
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .putAllAttributes(attributes)
                /*.putAttributes("uuid",uuid)
                .putAttributes("messageType",messageType)
                .putAttributes("messageFormat",messageFormat)
                .putAttributes("creationDate",date.toString().replaceAll("-",""))*/
                .setData(data)  // note: we set data anyway because some direct runner can't handle null data
                .build();
        ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);

        return messageIdFuture;
    }
}
