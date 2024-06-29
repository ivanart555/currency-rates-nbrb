package com.vadarod.external_api;

import com.vadarod.entities.Currency;
import com.vadarod.entities.Rate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;

@Service
public class NBRBApi implements CurrencyRatesAPI {
    private final String uri = "https://api.nbrb.by/exrates/";
    private final WebClient.Builder webClientBuilder;

    public NBRBApi(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public List<Currency> getCurrencies() {
        WebClient webClient = webClientBuilder.baseUrl(uri).build();
        List<Currency> currencies = webClient.get()
                .uri("currencies")
                .retrieve()
                .bodyToFlux(Currency.class)
                .collectList()
                .block();
        return currencies;
    }

    @Override
    public List<Rate> getRates(LocalDate date) {
        WebClient webClient = webClientBuilder.baseUrl(uri).build();
        List<Rate> rates = webClient.get()
                .uri("rates?ondate={date}&periodicity=0", date.toString())
                .retrieve()
                .bodyToFlux(Rate.class)
                .collectList()
                .block();
        return rates;
    }
}
