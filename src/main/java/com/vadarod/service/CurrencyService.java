package com.vadarod.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vadarod.dto.CurrencyDTO;
import com.vadarod.entities.Currency;
import com.vadarod.exception.CurrencyServiceException;
import com.vadarod.external_api.CurrencyRatesAPI;
import com.vadarod.repository.CurrencyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrencyService {
    private final CurrencyRatesAPI currencyRatesAPI;
    private final CurrencyRepository currencyRepository;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    public CurrencyService(CurrencyRatesAPI currencyRatesAPI, CurrencyRepository currencyRepository, ObjectMapper objectMapper) {
        this.currencyRatesAPI = currencyRatesAPI;
        this.currencyRepository = currencyRepository;
        this.objectMapper = objectMapper;
    }

    public void fetchAndSaveCurrencies() {
        logger.info("Starting to fetch and save currencies");

        String currenciesJson;
        try {
            logger.debug("Performing external API call to fetch currencies");
            currenciesJson = currencyRatesAPI.getCurrencies();
        } catch (Exception e) {
            logger.error("Error occurred while calling external API to fetch currencies", e);
            throw new CurrencyServiceException("Error fetching currencies from external API", e);
        }

        List<CurrencyDTO> currencyDTOs;
        try {
            logger.debug("Deserializing currencies JSON");
            currencyDTOs = objectMapper.readValue(currenciesJson, new TypeReference<>() {
            });
        } catch (Exception e) {
            logger.error("Error deserializing JSON for currencies", e);
            throw new CurrencyServiceException("Error deserializing JSON", e);
        }

        List<Currency> currencies = currencyDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());

        try {
            logger.debug("Saving currencies to the database");
            currencyRepository.saveAll(currencies);
            logger.info("Currencies fetched and saved successfully");
        } catch (Exception e) {
            logger.error("Error saving currencies to database", e);
            throw new CurrencyServiceException("Error saving currencies to database", e);
        }
    }

    private Currency convertToEntity(CurrencyDTO currencyDTO) {
        logger.debug("Converting CurrencyDTO to Currency entity for Cur_ID: {}", currencyDTO.getCurId());

        Currency currency = new Currency();
        currency.setCurId(currencyDTO.getCurId());
        currency.setName(currencyDTO.getName());
        currency.setCode(currencyDTO.getCode());
        currency.setAbbreviation(currencyDTO.getAbbreviation());

        logger.debug("Currency entity created successfully for Cur_ID: {}", currencyDTO.getCurId());

        return currency;
    }
}
