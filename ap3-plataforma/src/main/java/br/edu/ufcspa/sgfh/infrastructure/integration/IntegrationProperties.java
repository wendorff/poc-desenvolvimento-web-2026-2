package br.edu.ufcspa.sgfh.infrastructure.integration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("sgfh.integrations")
public record IntegrationProperties(
        Duration connectTimeout,
        Duration readTimeout,
        String userAgent,
        String anvisaBaseUrl,
        String rxnavBaseUrl,
        String brasilapiBaseUrl
) {
}
