package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig { // klasa koja definira koji REST API se mogu koristiti

    @Bean
    public RestClient dummyJsonClient(RestClient.Builder builder) {
        return builder
                .baseUrl("https://dummyjson.com")
                .build();
    }
}
