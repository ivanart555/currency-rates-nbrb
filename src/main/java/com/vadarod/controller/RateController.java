package com.vadarod.controller;

import com.vadarod.dto.RateDTO;
import com.vadarod.exception.RateServiceException;
import com.vadarod.service.RateService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/rates")
public class RateController {
    private final RateService rateService;

    public RateController(RateService rateService) {
        this.rateService = rateService;
    }

    @GetMapping("/update")
    public ResponseEntity<String> updateRates(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            rateService.updateRates(date);
            return ResponseEntity.ok(String.format("Currency rates loaded successfully for date: %s", date));
        } catch (RateServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Error updating rates: %s", e.getMessage()));
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> getRate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                     @RequestParam("currencyCode") String currencyCode) {
        try {
            RateDTO rateDTO = rateService.getCurrencyRate(date, currencyCode);
            return ResponseEntity.ok(rateDTO);
        } catch (RateServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("Error fetching currency rate for currency: %s and date: %s", currencyCode, date));
        }
    }
}
