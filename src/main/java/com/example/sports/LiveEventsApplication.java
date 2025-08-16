package com.example.sports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class LiveEventsApplication {

	public static void main(String[] args) {
		SpringApplication.run(LiveEventsApplication.class, args);
	}

}
