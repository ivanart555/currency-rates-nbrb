package com.vadarod.controller;

import com.vadarod.entities.Rate;
import com.vadarod.service.RateService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/rates")
public class RateController {
    private final RateService rateService;

    public RateController(RateService rateService) {
        this.rateService = rateService;
    }

    @GetMapping("/update")
    public String updateRates(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        rateService.updateRates(date);
        return "Data loaded successfully for date: " + date;
    }

    @GetMapping("/")
    public Rate getRate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                        @RequestParam String currencyCode) {

        Optional<Rate> rate = rateService.getCurrencyRate(date, currencyCode);
        return rate.orElse(null);
    }
}
