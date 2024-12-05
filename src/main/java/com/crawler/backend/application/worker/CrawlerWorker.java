package com.crawler.backend.application.worker;

import com.crawler.backend.domain.CrawlResult;
import com.crawler.backend.domain.CrawlStatus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlerWorker implements Runnable {
    private final CrawlResult crawlResult;
    private final String baseUrl;
    private final int maxResults;

    public CrawlerWorker(CrawlResult crawlResult, String baseUrl, int maxResults) {
        this.crawlResult = crawlResult;
        this.baseUrl = baseUrl;
        this.maxResults = maxResults;
    }

    @Override
    public void run() {
        Set<String> visitedUrls = new HashSet<>();
        Queue<String> toVisit = new LinkedList<>();
        toVisit.add(baseUrl);

        while (!toVisit.isEmpty() && crawlResult.getUrls().size() < maxResults) {
            String currentUrl = toVisit.poll();
            if (visitedUrls.contains(currentUrl)) {
                continue;
            }
            visitedUrls.add(currentUrl);

            try {
                String pageContent = fetchPageContent(currentUrl);

                if (pageContent.toLowerCase().contains(crawlResult.getKeyword().toLowerCase())) {
                    crawlResult.addUrl(currentUrl);
                }

                List<String> links = extractLinks(pageContent);
                for (String link : links) {
                    String absoluteUrl = resolveUrl(baseUrl, currentUrl, link);
                    if (absoluteUrl.startsWith(baseUrl) && !visitedUrls.contains(absoluteUrl)) {
                        toVisit.add(absoluteUrl);
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to process URL: " + currentUrl + " - " + e.getMessage());
            }
        }

        crawlResult.setStatus(CrawlStatus.DONE);
    }

    private String fetchPageContent(String urlString) throws Exception {
        StringBuilder content = new StringBuilder();
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }

    // Extract links from the html page
    private List<String> extractLinks(String html) {
        List<String> links = new ArrayList<>();
        Pattern linkPattern = Pattern.compile("href\\s*=\\s*\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);
        Matcher matcher = linkPattern.matcher(html);

        while (matcher.find()) {
            links.add(matcher.group(1));
        }
        return links;
    }

    // Convert from relative to absolute
    private String resolveUrl(String baseUrl, String currentUrl, String relativeUrl) {
        try {
            URL base = new URL(baseUrl);
            URL current = new URL(currentUrl);
            return new URL(current, relativeUrl).toString();
        } catch (Exception e) {
            return relativeUrl;
        }
    }
}
