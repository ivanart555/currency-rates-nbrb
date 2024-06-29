package com.vadarod.service;

import com.vadarod.entities.Currency;
import com.vadarod.entities.Rate;
import com.vadarod.external_api.CurrencyRatesAPI;
import com.vadarod.repository.CurrencyRepository;
import com.vadarod.repository.RateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RateService {
    private final CurrencyRatesAPI currencyRatesAPI;
    private final RateRepository rateRepository;
    private final CurrencyRepository currencyRepository;

    public RateService(RateRepository rateRepository, CurrencyRepository currencyRepository, CurrencyRatesAPI currencyRatesAPI) {
        this.rateRepository = rateRepository;
        this.currencyRepository = currencyRepository;
        this.currencyRatesAPI = currencyRatesAPI;
    }

    public boolean updateRates(LocalDate date) {
        List<Rate> rates = currencyRatesAPI.getRates(date);

        for (Rate rate : rates) {
            Currency currency = currencyRepository.findByCurId(rate.getCurrency().getCurId()).orElse(null);
            if (currency == null) {
                currency = rate.getCurrency();
                currencyRepository.save(currency);
            } else {
                rate.setCurrency(currency);
            }
            rateRepository.saveAll(rates);

        }
        return true;
    }

    public Optional<Rate> getCurrencyRate(LocalDate date, String currencyCode) {
        return rateRepository.findByDateAndCurrencyCode(date, currencyCode);
    }
}
