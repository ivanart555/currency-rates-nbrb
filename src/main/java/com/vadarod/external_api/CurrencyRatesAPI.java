package com.vadarod.external_api;

import com.vadarod.entities.Currency;
import com.vadarod.entities.Rate;

import java.time.LocalDate;
import java.util.List;

public interface CurrencyRatesAPI {

    List<Currency> getCurrencies();

    List<Rate> getRates(LocalDate date);
}
