package com.vadarod.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vadarod.dto.CurrencyDTO;
import com.vadarod.entities.Currency;
import com.vadarod.external_api.CurrencyRatesAPI;
import com.vadarod.repository.CurrencyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrencyService {
    private final CurrencyRatesAPI currencyRatesAPI;
    private final CurrencyRepository currencyRepository;
    private final ObjectMapper objectMapper;

    public CurrencyService(CurrencyRatesAPI currencyRatesAPI, CurrencyRepository currencyRepository, ObjectMapper objectMapper) {
        this.currencyRatesAPI = currencyRatesAPI;
        this.currencyRepository = currencyRepository;
        this.objectMapper = objectMapper;
    }

    public void fetchAndSaveCurrencies() {
        String currenciesJson = currencyRatesAPI.getCurrencies();

        List<CurrencyDTO> currencyDTOs;
        try {
            currencyDTOs = objectMapper.readValue(currenciesJson, new TypeReference<>() {
            });
        } catch (Exception e) {

            System.err.println("Error deserializing JSON: " + e.getMessage());
            throw new RuntimeException("Error deserializing JSON", e);
        }

        List<Currency> currencies = currencyDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        currencyRepository.saveAll(currencies);
    }

    private Currency convertToEntity(CurrencyDTO currencyDTO) {
        Currency currency = new Currency();
        currency.setCurId(currencyDTO.getCurId());
        currency.setName(currencyDTO.getName());
        currency.setCode(currencyDTO.getCode());
        currency.setAbbreviation(currencyDTO.getAbbreviation());

        return currency;
    }
}
