package com.crawler.backend.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CrawlResult {
    private final String id;
    private final String keyword;
    private final Set<String> urls;
    private CrawlStatus status;

    public CrawlResult(String id, String keyword) {
        this.id = id;
        this.keyword = keyword;
        this.urls = Collections.synchronizedSet(new HashSet<>());
        this.status = CrawlStatus.ACTIVE;
    }

    public String getId() {
        return id;
    }

    public String getKeyword() {
        return keyword;
    }

    public CrawlStatus getStatus() {
        return status;
    }

    public void setStatus(CrawlStatus status) {
        this.status = status;
    }

    public Set<String> getUrls() {
        return new HashSet<>(urls);
    }

    public void addUrl(String url) {
        urls.add(url);
    }
}
