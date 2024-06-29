package com.vadarod.external_api;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;

@Service
public class NBRBApi implements CurrencyRatesAPI {
    private final String uri = "https://api.nbrb.by/exrates/";
    private final WebClient.Builder webClientBuilder;

    public NBRBApi(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public String getCurrencies() {
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
