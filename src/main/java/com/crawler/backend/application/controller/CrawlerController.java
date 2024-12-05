package com.crawler.backend.application.controller;

import com.crawler.backend.application.service.CrawlerService;
import com.crawler.backend.application.utils.Options;
import com.crawler.backend.domain.CrawlResult;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.util.Map;

public class CrawlerController {
    private static final CrawlerService crawlerService = new CrawlerService();
    private static final Gson gson = new Gson();

    // POST /crawl
    public static spark.Route post = CrawlerController::handlePost;

    // GET /crawl/:id
    public static spark.Route get = CrawlerController::handleGet;

    private static Object handlePost(Request request, Response response) {
        Map<String, String> body = gson.fromJson(request.body(), Map.class);
        String keyword = body.get("keyword");

        if (keyword == null || keyword.length() < 4 || keyword.length() > 32) {
            response.status(400);
            return "Keyword must be between 4 and 32 characters.";
        }

        String crawlId = crawlerService.startCrawl(keyword, Options.BASE_URL, Options.MAX_RESULTS);

        response.status(200);
        response.type("application/json");
        return gson.toJson(Map.of("id", crawlId));
    }

    private static Object handleGet(Request request, Response response) {
        String crawlId = request.params(":id");
        CrawlResult result = crawlerService.getCrawlResult(crawlId);

        if (result == null) {
            response.status(404);
            return "Crawl ID not found.";
        }

        response.status(200);
        response.type("application/json");
        return gson.toJson(Map.of(
                "id", result.getId(),
                "status", result.getStatus().name().toLowerCase(),
                "urls", result.getUrls()
        ));
    }

    public static void shutdown() {
        crawlerService.shutdown();
    }
}
