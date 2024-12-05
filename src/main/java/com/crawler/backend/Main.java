package com.crawler.backend;

import com.crawler.backend.application.configuration.CrawlConfig;
import com.crawler.backend.application.controller.CrawlerController;
import com.crawler.backend.application.utils.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        configureApplication();

        post("/crawls", CrawlerController.post);
        get("/crawls/:id", CrawlerController.get);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down the application...");
            CrawlerController.shutdown();
        }));

        LOGGER.info("HTTP API initialized on port 4567");
    }

    private static void configureApplication() {
        try {
            Options.BASE_URL = CrawlConfig.baseUrl();
            LOGGER.info("Base URL set to: {}", Options.BASE_URL);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid base URL: {}", e.getMessage());
            System.exit(-1);
        }

        try {
            Options.MAX_RESULTS = CrawlConfig.maxResults();
            LOGGER.info("Max results set to: {}", Options.MAX_RESULTS);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid max results: {}", e.getMessage());
            System.exit(-1);
        }
    }
}
