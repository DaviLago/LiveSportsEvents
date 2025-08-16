package com.example.sports.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendEventToKafka(String topic, String eventJson) {
        log.info("Publishing message to topic '{}': {}", topic, eventJson);
        kafkaTemplate.send(topic, eventJson).thenAccept(success -> {
            log.info("Message sent successfully: {}", success.getProducerRecord());
        }).exceptionally(failure -> {
            log.error("Message failed to send: {}", failure.getMessage());
            return null;
        });
    }

}
