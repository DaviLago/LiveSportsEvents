package com.example.sports.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sports.enums.Status;
import com.example.sports.model.Event;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LiveService {

	public static final Integer EVENT_UPDATE_INTERVAL = 10000;

	@Autowired
	private ExternalService externalService;

	@Autowired
	private KafkaService kafkaService;

	private final Map<String, Thread> eventThreads = new ConcurrentHashMap<>();

	public void update(Event event) {
		if (event.getStatus().equals(Status.LIVE)) {
			log.info("Event {} is now live", event.getId());
			startLiveUpdatesForEvent(event);
		} else {
			log.info("Event {} is no longer live", event.getId());
			stopLiveUpdatesForEvent(event.getId());
		}
	}

	private void startLiveUpdatesForEvent(Event event) {
		if (eventThreads.containsKey(event.getId())) {
			log.warn("Live updates for event {} are already running", event.getId());
			return;
		}
		log.info("Starting live updates for event {}", event.getId());
		eventThreads.put(event.getId(), startNewThread(event));
	}

	private Thread startNewThread(Event event) {
		Thread thread = Thread.ofVirtual().start(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					externalService.fetchEventInfo(event.getId())
							.ifPresent(info -> kafkaService.sendEventToKafka("live-events", info.toJson()));
					Thread.sleep(EVENT_UPDATE_INTERVAL);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
		return thread;
	}

	private void stopLiveUpdatesForEvent(String eventId) {
		Thread thread = eventThreads.remove(eventId);
		if (thread != null) {
			thread.interrupt();
		}
		log.info("Stopped live updates for event {}", eventId);
	}

}
