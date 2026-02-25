package com.relatospapel.ms_books_catalogue.controller;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalogue")
public class TestController {

    private final OpenSearchClient client;

    public TestController(OpenSearchClient client) {
        this.client = client;
    }

    @GetMapping("/test-es")
    public String testConnection() throws Exception {
        return "Conectado a cluster: " + client.info().clusterName();
    }
}