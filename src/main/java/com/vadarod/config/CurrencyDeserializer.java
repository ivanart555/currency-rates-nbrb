package com.vadarod.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.vadarod.entities.Currency;
import com.vadarod.repository.CurrencyRepository;

import java.io.IOException;

public class CurrencyDeserializer extends JsonDeserializer<Currency> {
    private final CurrencyRepository currencyRepository;

    public CurrencyDeserializer(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public Currency deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Long curId = p.getLongValue();
        return currencyRepository.findByCurId(curId).orElseGet(() -> {
            Currency newCurrency = new Currency();
            newCurrency.setCurId(curId);
            return newCurrency;
        });
    }
}
