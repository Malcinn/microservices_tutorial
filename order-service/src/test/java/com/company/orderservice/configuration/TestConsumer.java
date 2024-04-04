package com.company.orderservice.configuration;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.concurrent.CountDownLatch;


public class TestConsumer {

    private CountDownLatch latch = new CountDownLatch(1);

    private String payload;

    @KafkaListener(topics = "${order.service.topicName}", groupId = "testGroup")
    public void onMessageListener(ConsumerRecord<?, ?> consumerRecord){
        payload = consumerRecord.toString();
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public String getPayload() {
        return payload;
    }
}
