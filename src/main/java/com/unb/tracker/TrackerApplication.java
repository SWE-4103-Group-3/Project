package com.unb.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class TrackerApplication {
	private static final Logger LOG = LoggerFactory.getLogger(TrackerApplication.class);
	public static void main(String[] args) {
		LOG.info("main - starting");
		SpringApplication.run(TrackerApplication.class, args);
	}
}
