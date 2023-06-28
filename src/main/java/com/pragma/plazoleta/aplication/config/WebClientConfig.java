package com.pragma.plazoleta.aplication.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Autowired
    HeaderRequest headerRequest;

    public WebClient request(){
        return WebClient.builder()
                .baseUrl(headerRequest.headers().getUrlBase())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
