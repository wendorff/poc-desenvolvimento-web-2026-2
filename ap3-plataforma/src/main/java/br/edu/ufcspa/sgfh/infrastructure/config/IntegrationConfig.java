package br.edu.ufcspa.sgfh.infrastructure.config;

import br.edu.ufcspa.sgfh.infrastructure.integration.AnvisaBularioClient;
import br.edu.ufcspa.sgfh.infrastructure.integration.BrasilApiCnpjClient;
import br.edu.ufcspa.sgfh.infrastructure.integration.IntegrationProperties;
import br.edu.ufcspa.sgfh.infrastructure.integration.RxNavClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(IntegrationProperties.class)
public class IntegrationConfig {

    private final IntegrationProperties properties;

    public IntegrationConfig(IntegrationProperties properties) {
        this.properties = properties;
    }

    @Bean
    public AnvisaBularioClient anvisaBularioClient() {
        return new AnvisaBularioClient(restClient(), properties.anvisaBaseUrl());
    }

    @Bean
    public RxNavClient rxNavClient() {
        return new RxNavClient(restClient(), properties.rxnavBaseUrl());
    }

    @Bean
    public BrasilApiCnpjClient brasilApiCnpjClient() {
        return new BrasilApiCnpjClient(restClient(), properties.brasilapiBaseUrl());
    }

    private RestClient.Builder restClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.connectTimeout());
        requestFactory.setReadTimeout(properties.readTimeout());
        return RestClient.builder()
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.USER_AGENT, properties.userAgent());
    }
}
