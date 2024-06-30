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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RateServiceTest {

    @Mock
    private RateRepository rateRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private CurrencyRatesAPI currencyRatesAPI;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RateService rateService;

    private final LocalDate date = LocalDate.of(2023, 6, 30);

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testUpdateRatesSuccess() throws Exception {
        String ratesJson = """
                [{"Cur_ID":431,"Date":"2024-06-29T00:00:00","Cur_Abbreviation":"USD","Cur_Scale":1,"Cur_Name":"Доллар США","Cur_OfficialRate":3.1624},\
                {"Cur_ID":512,"Date":"2024-06-29T00:00:00","Cur_Abbreviation":"VND","Cur_Scale":100000,"Cur_Name":"Донгов","Cur_OfficialRate":12.4211},\
                {"Cur_ID":451,"Date":"2024-06-29T00:00:00","Cur_Abbreviation":"EUR","Cur_Scale":1,"Cur_Name":"Евро","Cur_OfficialRate":3.3821}]""";
        when(currencyRatesAPI.getRates(date)).thenReturn(ratesJson);

        RateDTO rateDTO = new RateDTO();
        rateDTO.setCurId(431L);
        rateDTO.setDate(date);
        rateDTO.setRate(BigDecimal.valueOf(3.1624));
        rateDTO.setScale(1);
        when(objectMapper.readValue(eq(ratesJson), any(TypeReference.class))).thenReturn(Arrays.asList(rateDTO));

        Currency currency = new Currency();
        currency.setCurId(431L);
        when(currencyRepository.findByCurId(431L)).thenReturn(Optional.of(currency));

        Rate rate = new Rate();
        rate.setCurrency(currency);
        rate.setDate(date);
        rate.setRate(BigDecimal.valueOf(3.1624));
        rate.setScale(1);

        rateService.updateRates(date);

        verify(rateRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testUpdateRatesApiCallFailure() {
        when(currencyRatesAPI.getRates(date)).thenThrow(new RuntimeException("API failure"));

        RateServiceException exception = assertThrows(RateServiceException.class, () -> rateService.updateRates(date));
        assertEquals("Error fetching rates from external API", exception.getMessage());
    }

    @Test
    public void testGetCurrencyRateSuccess() {
        Currency currency = new Currency();
        currency.setCurId(431L);
        currency.setAbbreviation("USD");
        currency.setName("Доллар США");

        Rate rate = new Rate();
        rate.setCurrency(currency);
        rate.setDate(date);
        rate.setRate(BigDecimal.valueOf(3.1624));
        rate.setScale(1);

        when(rateRepository.findByDateAndCurrencyCode(date, "840")).thenReturn(Optional.of(rate));

        RateDTO rateDTO = rateService.getCurrencyRate(date, "840");

        assertNotNull(rateDTO);
        assertEquals(431L, rateDTO.getCurId());
        assertEquals("USD", rateDTO.getAbbreviation());
        assertEquals("Доллар США", rateDTO.getName());
        assertEquals(date, rateDTO.getDate());
        assertEquals(BigDecimal.valueOf(3.1624), rateDTO.getRate());
        assertEquals(1, rateDTO.getScale());
    }

    @Test
    public void testGetCurrencyRateNotFound() {
        when(rateRepository.findByDateAndCurrencyCode(date, "840")).thenReturn(Optional.empty());

        RateServiceException exception = assertThrows(RateServiceException.class, () -> rateService.getCurrencyRate(date, "840"));
        assertEquals(String.format("Rate for currency: %s and date: %s not found", "840", date), exception.getMessage());
    }
}