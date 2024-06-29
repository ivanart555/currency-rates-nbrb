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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(RateService.class);

    public RateService(RateRepository rateRepository, CurrencyRepository currencyRepository, CurrencyRatesAPI currencyRatesAPI, ObjectMapper objectMapper) {
        this.rateRepository = rateRepository;
        this.currencyRepository = currencyRepository;
        this.currencyRatesAPI = currencyRatesAPI;
        this.objectMapper = objectMapper;
    }

    public void updateRates(LocalDate date) {
        logger.info("Starting update currency rates for date: {}", date);

        String ratesJson;
        try {
            logger.debug("Performing external api call");
            ratesJson = currencyRatesAPI.getRates(date);
        } catch (Exception e) {
            logger.error("Error occurred while calling external api for currency rates on {}", date, e);
            throw new RateServiceException("Error fetching rates from external API", e);
        }

        List<RateDTO> rateDTOs;
        try {
            logger.debug("Deserializing rates JSON");
            rateDTOs = objectMapper.readValue(ratesJson, new TypeReference<>() {
            });
        } catch (Exception e) {
            logger.error("Error deserializing JSON for rates on {}", date, e);
            throw new RateServiceException("Error deserializing JSON", e);
        }

        List<Rate> rates = rateDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());

        try {
            logger.debug("Saving rates to the database");
            rateRepository.saveAll(rates);
            logger.info("Rates updated successfully for date: {}", date);
        } catch (Exception e) {
            logger.error("Error saving rates to database for date: {}", date, e);
            throw new RateServiceException("Error saving rates to database", e);
        }
    }

    public RateDTO getCurrencyRate(LocalDate date, String currencyCode) {
        logger.info("Fetching rate for date: {} and currency code: {}", date, currencyCode);

        Rate rate;
        try {
            rate = rateRepository.findByDateAndCurrencyCode(date, currencyCode).orElse(null);
        } catch (Exception e) {
            logger.error("Error fetching currency rate from database for date: {} and currency code: {}", date, currencyCode, e);
            throw new RateServiceException("Error fetching rate from database", e);
        }

        if (rate == null) {
            logger.warn("Rate not found for currency: {} and date: {}", currencyCode, date);
            throw new RateServiceException(String.format("Rate for currency: %s and date: %s not found", currencyCode, date));
        }

        logger.info("Rate fetched successfully for date: {} and currency code: {}", date, currencyCode);
        return convertToDTO(rate);
    }

    private Rate convertToEntity(RateDTO rateDTO) {
        logger.debug("Converting RateDTO to Rate entity for Cur_ID: {}", rateDTO.getCurId());

        Currency currency;
        try {
            currency = currencyRepository.findByCurId(rateDTO.getCurId()).orElse(null);
        } catch (Exception e) {
            logger.error("Failed to get currency from database with id: {}", rateDTO.getCurId(), e);
            throw new RateServiceException(String.format("Failed to get currency from database with id: %s", rateDTO.getCurId()), e);
        }

        if (currency == null) {
            logger.warn("Currency with id: {} not found in database", rateDTO.getCurId());
            throw new RateServiceException(String.format("Currency with id: %s not found in database", rateDTO.getCurId()));
        }

        Rate rate = new Rate();
        rate.setCurrency(currency);
        rate.setDate(rateDTO.getDate());
        rate.setRate(rateDTO.getRate());
        rate.setScale(rateDTO.getScale());

        logger.debug("Rate entity created successfully for Cur_ID: {}", rateDTO.getCurId());
        return rate;
    }

    private RateDTO convertToDTO(Rate rate) {
        logger.debug("Converting Rate entity to RateDTO for Cur_ID: {}", rate.getCurrency().getCurId());

        RateDTO rateDTO = new RateDTO();
        rateDTO.setDate(rate.getDate());
        rateDTO.setRate(rate.getRate());
        rateDTO.setScale(rate.getScale());
        rateDTO.setCurId(rate.getCurrency().getCurId());
        rateDTO.setAbbreviation(rate.getCurrency().getAbbreviation());
        rateDTO.setName(rate.getCurrency().getName());

        logger.debug("RateDTO created successfully for Cur_ID: {}", rate.getCurrency().getCurId());

        return rateDTO;
    }
}
