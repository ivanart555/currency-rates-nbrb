package com.vadarod.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vadarod.dto.RateDTO;
import com.vadarod.entities.Currency;
import com.vadarod.entities.Rate;
import com.vadarod.exception.RateServiceException;
import com.vadarod.external_api.CurrencyRatesAPI;
import com.vadarod.repository.CurrencyRepository;
import com.vadarod.repository.RateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RateService {
    private final CurrencyRatesAPI currencyRatesAPI;
    private final RateRepository rateRepository;
    private final CurrencyRepository currencyRepository;
    private final ObjectMapper objectMapper;

    public RateService(RateRepository rateRepository, CurrencyRepository currencyRepository, CurrencyRatesAPI currencyRatesAPI, ObjectMapper objectMapper) {
        this.rateRepository = rateRepository;
        this.currencyRepository = currencyRepository;
        this.currencyRatesAPI = currencyRatesAPI;
        this.objectMapper = objectMapper;
    }

    public boolean updateRates(LocalDate date) {
        String ratesJson;
        try {
            ratesJson = currencyRatesAPI.getRates(date);
        } catch (Exception e) {
            throw new RateServiceException("Error fetching rates from external API", e);
        }

        List<RateDTO> rateDTOs;
        try {
            rateDTOs = objectMapper.readValue(ratesJson, new TypeReference<>() {
            });
        } catch (Exception e) {
            System.err.println("Error deserializing JSON: " + e.getMessage());
            throw new RateServiceException("Error deserializing JSON", e);
        }

        List<Rate> rates = rateDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());

        try {
            rateRepository.saveAll(rates);
        } catch (Exception e) {
            throw new RateServiceException("Error saving rates to database", e);
        }
        return true;
    }

    public RateDTO getCurrencyRate(LocalDate date, String currencyCode) {
        Rate rate;
        try {
            rate = rateRepository.findByDateAndCurrencyCode(date, currencyCode).orElse(null);
        } catch (Exception e) {
            throw new RateServiceException("Error fetching rate from database", e);
        }

        if (rate == null) {
            throw new RateServiceException(String.format("Rate for currency: %s and date: %s not found", currencyCode, date));
        }

        return convertToDTO(rate);
    }

    private Rate convertToEntity(RateDTO rateDTO) {
        Currency currency;
        try {
            currency = currencyRepository.findByCurId(rateDTO.getCurId()).orElse(null);
        } catch (Exception e) {
            throw new RateServiceException(String.format("Failed to get currency from database with id: %s", rateDTO.getCurId()), e);
        }

        if (currency == null) {
            throw new RateServiceException(String.format("Currency with id: %s not found in database", rateDTO.getCurId()));
        }

        Rate rate = new Rate();
        rate.setCurrency(currency);
        rate.setDate(rateDTO.getDate());
        rate.setRate(rateDTO.getRate());
        rate.setScale(rateDTO.getScale());
        return rate;
    }

    private RateDTO convertToDTO(Rate rate) {

        RateDTO rateDTO = new RateDTO();
        rateDTO.setDate(rate.getDate());
        rateDTO.setRate(rate.getRate());
        rateDTO.setScale(rate.getScale());
        rateDTO.setCurId(rate.getCurrency().getCurId());
        rateDTO.setAbbreviation(rate.getCurrency().getAbbreviation());
        rateDTO.setName(rate.getCurrency().getName());
        return rateDTO;
    }
}
