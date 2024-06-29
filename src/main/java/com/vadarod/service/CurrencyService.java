package com.vadarod.service;

import com.vadarod.entities.Currency;
import com.vadarod.external_api.CurrencyRatesAPI;
import com.vadarod.repository.CurrencyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyService {
    private final CurrencyRatesAPI currencyRatesAPI;
    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRatesAPI currencyRatesAPI, CurrencyRepository currencyRepository) {
        this.currencyRatesAPI = currencyRatesAPI;
        this.currencyRepository = currencyRepository;
    }

    public void fetchAndSaveCurrencies() {
        List<Currency> allCurrencies = currencyRatesAPI.getCurrencies();
        currencyRepository.saveAll(allCurrencies);
    }

    public List<Currency> findAllCurrencies() {
        return currencyRepository.findAll();
    }
}
