package com.unb.tracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TrackerApplication {
    private static final Logger LOG = LoggerFactory.getLogger(TrackerApplication.class);

    public static void main(String[] args) {
        LOG.info("main - starting");
        SpringApplication.run(TrackerApplication.class, args);
    }
}
