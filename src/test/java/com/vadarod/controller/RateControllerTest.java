package com.vadarod.controller;

import com.vadarod.dto.RateDTO;
import com.vadarod.exception.RateServiceException;
import com.vadarod.service.RateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RateController.class)
@ExtendWith(MockitoExtension.class)
public class RateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RateService rateService;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testUpdateRatesSuccess() throws Exception {
        LocalDate date = LocalDate.of(2024, 6, 29);

        doNothing().when(rateService).updateRates(date);

        mockMvc.perform(get("/api/v1/rates/update")
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Currency rates loaded successfully for date: 2024-06-29"));
    }

    @Test
    public void testUpdateRatesFutureDate() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(1);

        mockMvc.perform(get("/api/v1/rates/update")
                        .param("date", futureDate.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Date cannot be in the future"));
    }

    @Test
    public void testUpdateRatesServiceException() throws Exception {
        LocalDate date = LocalDate.of(2024, 6, 29);

        doThrow(new RateServiceException("Service error")).when(rateService).updateRates(date);

        mockMvc.perform(get("/api/v1/rates/update")
                        .param("date", date.toString()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error updating rates: Service error"));
    }

    @Test
    public void testGetRateSuccess() throws Exception {
        LocalDate date = LocalDate.of(2024, 6, 29);
        String currencyCode = "643";

        RateDTO rateDTO = new RateDTO();
        rateDTO.setCurId(456L);
        rateDTO.setDate(date);
        rateDTO.setRate(BigDecimal.valueOf(3.68));
        rateDTO.setScale(100);
        rateDTO.setAbbreviation("RUB");
        rateDTO.setName("Российский рубль");

        when(rateService.getCurrencyRate(date, currencyCode)).thenReturn(rateDTO);

        mockMvc.perform(get("/api/v1/rates/")
                        .param("date", date.toString())
                        .param("currencyCode", currencyCode))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "Cur_ID": 456,
                            "Date": "2024-06-29",
                            "Cur_OfficialRate": 3.68,
                            "Cur_Scale": 100,
                            "Cur_Abbreviation": "RUB",
                            "Cur_Name": "Российский рубль"
                        }"""));
    }

    @Test
    public void testGetRateFutureDate() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        String currencyCode = "643";

        mockMvc.perform(get("/api/v1/rates/")
                        .param("date", futureDate.toString())
                        .param("currencyCode", currencyCode))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Date cannot be in the future"));
    }

    @Test
    public void testGetRateNotFound() throws Exception {
        LocalDate date = LocalDate.of(2024, 6, 29);
        String currencyCode = "643";

        when(rateService.getCurrencyRate(date, currencyCode)).thenThrow(new RateServiceException("Rate not found"));

        mockMvc.perform(get("/api/v1/rates/")
                        .param("date", date.toString())
                        .param("currencyCode", currencyCode))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Currency rate for currency: 643 and date: 2024-06-29 not found in database."));
    }
}