package com.example.sports.controller;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import com.example.sports.service.LiveService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;

public class EventsControllerTest extends AbstractControllerTest {

    private static final String EVENTS_API = "/v1/events";

    @Value("classpath:request/new-live-event.json")
    private Resource newLiveEventResource;

    @Value("classpath:request/stop-live-event.json")
    private Resource stopLiveEventResource;

    @Value("classpath:kafka/event-update-message.json")
    private Resource eventUpdateResource;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Test
    void testLiveEventsEndToEnd() throws Exception {
        // New live event JSON data
        String newLiveEvent = newLiveEventResource.getContentAsString(StandardCharsets.UTF_8);

        // Send a request to create a new event with status LIVE
        mockMvc.perform(post(EVENTS_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newLiveEvent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("123"))
                .andExpect(jsonPath("$.status").value("LIVE"));

        // Assert that a live event update message is published to the Kafka topic "live-events"
        String eventUpdateMessage = eventUpdateResource.getContentAsString(StandardCharsets.UTF_8);
        try (Consumer<String, String> consumer = new KafkaConsumer<>(
                KafkaTestUtils.consumerProps("test", "true", embeddedKafka))) {
            consumer.subscribe(Collections.singletonList("live-events"));
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
            assertFalse(records.isEmpty());
            assertEquals(eventUpdateMessage, records.iterator().next().value());
        }

        // Stop live event JSON data
        String stopLiveEvent = stopLiveEventResource.getContentAsString(StandardCharsets.UTF_8);

        // Send a request to update the event status to NOT_LIVE
        mockMvc.perform(post(EVENTS_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(stopLiveEvent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("123"))
                .andExpect(jsonPath("$.status").value("NOT_LIVE"));

        // Assert that no live event update message is published to the Kafka topic "live-events"
        try (Consumer<String, String> consumer = new KafkaConsumer<>(
                KafkaTestUtils.consumerProps("test", "true", embeddedKafka))) {
            consumer.subscribe(Collections.singletonList("live-events"));
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(LiveService.EVENT_UPDATE_INTERVAL));
            assertTrue(records.isEmpty());
        }

    }

}
