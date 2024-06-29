package com.vadarod.controller;

import com.vadarod.dto.RateDTO;
import com.vadarod.exception.RateServiceException;
import com.vadarod.service.RateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/rates")
@Validated
@Tag(name = "Currency rates API", description = "Get and store currency rates from bank api")
public class RateController {
    private final RateService rateService;
    private static final Logger logger = LoggerFactory.getLogger(RateController.class);

    public RateController(RateService rateService) {
        this.rateService = rateService;
    }

    @GetMapping("/update")
    @Operation(summary = "Update the currency rates for a specific date",
            description = "Updates the currency rates for the given date amd stores it in local database for further use.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Currency rates loaded successfully",
                            content = @Content(mediaType = "text/plain",
                                    examples = @ExampleObject(value = "Currency rates loaded successfully for date: 2024-06-29"))),
                    @ApiResponse(responseCode = "400", description = "Error: Date cannot be in the future",
                            content = @Content(mediaType = "text/plain",
                                    examples = @ExampleObject(value = "Error: Date cannot be in the future"))),
                    @ApiResponse(responseCode = "500", description = "Error updating rates",
                            content = @Content(mediaType = "text/plain",
                                    examples = @ExampleObject(value = "Error updating rates: detailed error message")))
            })
    public ResponseEntity<String> updateRates(
            @Parameter(description = "The date for which the rates should be updated", example = "2024-06-29")
            @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        logger.info("Request to update rates for date: {}", date);

        if (date.isAfter(LocalDate.now())) {
            logger.warn("Attempt to update rates with a future date: {}", date);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: Date cannot be in the future");
        }

        try {
            rateService.updateRates(date);
            logger.info("Rates updated successfully for date: {}", date);
            return ResponseEntity.ok(String.format("Currency rates loaded successfully for date: %s", date));
        } catch (RateServiceException e) {
            logger.error("Error updating rates for date: {}", date, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Error updating rates: %s", e.getMessage()));
        }
    }

    @GetMapping("/")
    @Operation(summary = "Get currency rate by date and currency code",
            description = "Retrieves the currency rate for the given date and currency code from local database.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Currency rate retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\n" +
                                            "  \"Cur_ID\": 456,\n" +
                                            "  \"Date\": \"2024-06-29\",\n" +
                                            "  \"Cur_OfficialRate\": 3.68,\n" +
                                            "  \"Cur_Scale\": 100,\n" +
                                            "  \"Cur_Abbreviation\": \"RUB\",\n" +
                                            "  \"Cur_Name\": \"Российский рубль\"\n" +
                                            "}"))),
                    @ApiResponse(responseCode = "400", description = "Error: Date cannot be in the future",
                            content = @Content(mediaType = "text/plain",
                                    examples = @ExampleObject(value = "{\"error\": \"Error: Date cannot be in the future\"}"))),
                    @ApiResponse(responseCode = "404", description = "Currency rate not found",
                            content = @Content(mediaType = "text/plain",
                                    examples = @ExampleObject(value = "{\"error\": \"Currency rate for currency: 643 and date: 2024-06-29 not found in database.\"}")))
            })
    public ResponseEntity<?> getRate(
            @Parameter(description = "The date for which the rate is requested", example = "2024-06-29")
            @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "The 3-digit currency code", example = "643")
            @RequestParam(name = "currencyCode") @Pattern(regexp = "\\d{3}",
                    message = "Invalid currency code format. Must contain 3 numbers.") String currencyCode) {
        logger.info("Request to get currency rate for date: {} and currency code: {}", date, currencyCode);

        if (date.isAfter(LocalDate.now())) {
            logger.warn("Attempt to get rates with a future date: {}", date);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: Date cannot be in the future");
        }

        try {
            RateDTO rateDTO = rateService.getCurrencyRate(date, currencyCode);
            logger.info("Rate retrieved successfully for date: {} and currency code: {}", date, currencyCode);
            return ResponseEntity.ok(rateDTO);
        } catch (RateServiceException e) {
            logger.error("Error retrieving rate for date: {} and currency code: {}", date, currencyCode, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("Currency rate for currency: %s and date: %s not found in database.", currencyCode, date));
        }
    }
}
