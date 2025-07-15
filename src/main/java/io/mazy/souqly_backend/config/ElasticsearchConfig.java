package io.mazy.souqly_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "io.mazy.souqly_backend.repository.elasticsearch")
public class ElasticsearchConfig {
    // Configuration par défaut de Spring Boot Elasticsearch
    // Utilise les propriétés standard : spring.elasticsearch.uris
} 