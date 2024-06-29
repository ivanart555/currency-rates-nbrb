package com.vadarod.external_api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;

@Service
public class NBRBApi implements CurrencyRatesAPI {
    private final String uri = "https://api.nbrb.by/exrates/";
    private final WebClient.Builder webClientBuilder;
    private static final Logger logger = LoggerFactory.getLogger(NBRBApi.class);

    public NBRBApi(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public String getCurrencies() {
        logger.info("Request to get all currencies from api.nbrb.by");
        WebClient webClient = webClientBuilder.baseUrl(uri).build();
        return webClient.get()
                .uri("currencies")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Override
    public String getRates(LocalDate date) {
        WebClient webClient = webClientBuilder.baseUrl(uri).build();
        logger.info("Request to get currency rates from api.nbrb.by");
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("rates")
                        .queryParam("ondate", date.toString())
                        .queryParam("periodicity", "0")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
