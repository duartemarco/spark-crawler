package com.crawler.backend.application.service;

import com.crawler.backend.application.worker.CrawlerWorker;
import com.crawler.backend.domain.CrawlResult;

import java.util.Map;
import java.util.concurrent.*;

public class CrawlerService {
    private static final Map<String, CrawlResult> crawlResults = new ConcurrentHashMap<>();
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public String startCrawl(String keyword, String baseUrl, int maxResults) {
        String crawlId = generateCrawlId();
        CrawlResult result = new CrawlResult(crawlId, keyword);
        crawlResults.put(crawlId, result);
        executor.submit(new CrawlerWorker(result, baseUrl, maxResults));
        return crawlId;
    }

    public CrawlResult getCrawlResult(String crawlId) {
        return crawlResults.get(crawlId);
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private String generateCrawlId() {
        String chars = "abcdefghijklmnopqrstuvxyz0123456789";
        StringBuilder id = new StringBuilder();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 8; i++) {
            id.append(chars.charAt(random.nextInt(chars.length())));
        }
        return id.toString();
    }
}
