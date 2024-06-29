package com.vadarod.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vadarod.dto.RateDTO;
import com.vadarod.entities.Currency;
import com.vadarod.entities.Rate;
import com.vadarod.external_api.CurrencyRatesAPI;
import com.vadarod.repository.CurrencyRepository;
import com.vadarod.repository.RateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
        String ratesJson = currencyRatesAPI.getRates(date);

        List<RateDTO> rateDTOs;
        try {
            rateDTOs = objectMapper.readValue(ratesJson, new TypeReference<>() {
            });
        } catch (Exception e) {

            System.err.println("Error deserializing JSON: " + e.getMessage());
            throw new RuntimeException("Error deserializing JSON", e);
        }

        List<Rate> rates = rateDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        rateRepository.saveAll(rates);
        return true;
    }

    public Optional<Rate> getCurrencyRate(LocalDate date, String currencyCode) {
        return rateRepository.findByDateAndCurrencyCode(date, currencyCode);
    }

    private Rate convertToEntity(RateDTO rateDTO) {
        Currency currency = currencyRepository.findByCurId(rateDTO.getCurId()).orElse(null);

        Rate rate = new Rate();
        rate.setCurrency(currency);
        rate.setDate(rateDTO.getDate());
        rate.setRate(rateDTO.getRate());
        rate.setScale(rateDTO.getScale());
        return rate;
    }
}
