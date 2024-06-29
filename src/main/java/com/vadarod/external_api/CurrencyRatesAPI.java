package com.vadarod.external_api;

import java.time.LocalDate;

public interface CurrencyRatesAPI {

    String getCurrencies();

    String getRates(LocalDate date);
}
