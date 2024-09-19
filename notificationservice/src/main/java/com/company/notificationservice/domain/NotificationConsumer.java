package com.company.notificationservice.domain;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationConsumer.class);

    @KafkaListener(topics = "${notification.service.topicName}", groupId = "testGroup")
    public void consumeNotification(ConsumerRecord<?, ?> consumerRecord) {
        LOGGER.info("Message received: {}", consumerRecord.toString());
    }
}
