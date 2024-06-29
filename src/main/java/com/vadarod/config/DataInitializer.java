package com.vadarod.config;

import com.vadarod.service.CurrencyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final CurrencyService currencyService;

    public DataInitializer(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Override
    public void run(String... args) throws Exception {
        currencyService.fetchAndSaveCurrencies();
    }
}
