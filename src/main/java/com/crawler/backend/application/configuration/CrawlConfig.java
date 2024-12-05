package com.crawler.backend.application.configuration;

public class CrawlConfig {

    public static String baseUrl() {
        String baseUrl = System.getenv("BASE_URL");
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new IllegalArgumentException("BASE_URL is not set");
        }
        return baseUrl;
    }

    public static int maxResults() {
        String maxResults = System.getenv("MAX_RESULTS");
        if (maxResults == null || maxResults.isEmpty()) {
            return 100;
        }
        try {
            return Integer.parseInt(maxResults);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("MAX_RESULTS must be an integer");
        }
    }
}
