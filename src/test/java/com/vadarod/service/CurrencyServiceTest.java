package com.vadarod.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vadarod.dto.CurrencyDTO;
import com.vadarod.entities.Currency;
import com.vadarod.exception.CurrencyServiceException;
import com.vadarod.external_api.CurrencyRatesAPI;
import com.vadarod.repository.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {

    @Mock
    private CurrencyRatesAPI currencyRatesAPI;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testFetchAndSaveCurrenciesSuccess() throws Exception {
        String currenciesJson = """
                [{"Cur_ID":141,"Cur_ParentID":141,"Cur_Code":"810","Cur_Abbreviation":"RUR","Cur_Name":"Российский рубль",
                "Cur_Name_Bel":"Расійскі рубель","Cur_Name_Eng":"Russian Ruble","Cur_QuotName":"1 российский рубль",
                "Cur_QuotName_Bel":"1 расійскі рубель","Cur_QuotName_Eng":"1 Russian Ruble","Cur_NameMulti":"российский рубль",
                "Cur_Name_BelMulti":"расійскі рубель","Cur_Name_EngMulti":"Russian Ruble","Cur_Scale":1,"Cur_Periodicity":0,
                "Cur_DateStart":"1991-01-01T00:00:00","Cur_DateEnd":"2002-12-31T00:00:00"}]""";
        when(currencyRatesAPI.getCurrencies()).thenReturn(currenciesJson);

        CurrencyDTO currencyDTO = new CurrencyDTO();
        currencyDTO.setCurId(141L);
        currencyDTO.setName("Российский рубль");
        currencyDTO.setAbbreviation("RUR");
        currencyDTO.setCode("810");
        when(objectMapper.readValue(eq(currenciesJson), any(TypeReference.class))).thenReturn(Arrays.asList(currencyDTO));

        Currency currency = new Currency();
        currency.setCurId(141L);
        currency.setName("Российский рубль");
        currency.setAbbreviation("RUR");
        currency.setCode("810");

        currencyService.fetchAndSaveCurrencies();

        verify(currencyRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testFetchAndSaveCurrenciesApiCallFailure() {
        when(currencyRatesAPI.getCurrencies()).thenThrow(new RuntimeException("API failure"));

        CurrencyServiceException exception = assertThrows(CurrencyServiceException.class, () -> currencyService.fetchAndSaveCurrencies());
        assertEquals("Error fetching currencies from external API", exception.getMessage());
    }

    @Test
    public void testFetchAndSaveCurrenciesDeserializationFailure() throws Exception {
        String currenciesJson = """
                [{"Cur_ID":141,"Cur_ParentID":141,"Cur_Code":"810","Cur_Abbreviation":"RUR","Cur_Name":"Российский рубль",
                "Cur_Name_Bel":"Расійскі рубель","Cur_Name_Eng":"Russian Ruble","Cur_QuotName":"1 российский рубль",
                "Cur_QuotName_Bel":"1 расійскі рубель","Cur_QuotName_Eng":"1 Russian Ruble","Cur_NameMulti":"российский рубль",
                "Cur_Name_BelMulti":"расійскі рубель","Cur_Name_EngMulti":"Russian Ruble","Cur_Scale":1,"Cur_Periodicity":0,
                "Cur_DateStart":"1991-01-01T00:00:00","Cur_DateEnd":"2002-12-31T00:00:00"}]""";
        when(currencyRatesAPI.getCurrencies()).thenReturn(currenciesJson);

        when(objectMapper.readValue(eq(currenciesJson), any(TypeReference.class))).thenThrow(new RuntimeException("Deserialization failure"));

        CurrencyServiceException exception = assertThrows(CurrencyServiceException.class, () -> currencyService.fetchAndSaveCurrencies());
        assertEquals("Error deserializing JSON", exception.getMessage());
    }

    @Test
    public void testFetchAndSaveCurrenciesDatabaseSaveFailure() throws Exception {
        String currenciesJson = """
                [{"Cur_ID":141,"Cur_ParentID":141,"Cur_Code":"810","Cur_Abbreviation":"RUR","Cur_Name":"Российский рубль",
                "Cur_Name_Bel":"Расійскі рубель","Cur_Name_Eng":"Russian Ruble","Cur_QuotName":"1 российский рубль",
                "Cur_QuotName_Bel":"1 расійскі рубель","Cur_QuotName_Eng":"1 Russian Ruble","Cur_NameMulti":"российский рубль",
                "Cur_Name_BelMulti":"расійскі рубель","Cur_Name_EngMulti":"Russian Ruble","Cur_Scale":1,"Cur_Periodicity":0,
                "Cur_DateStart":"1991-01-01T00:00:00","Cur_DateEnd":"2002-12-31T00:00:00"}]""";
        when(currencyRatesAPI.getCurrencies()).thenReturn(currenciesJson);

        CurrencyDTO currencyDTO = new CurrencyDTO();
        currencyDTO.setCurId(141L);
        currencyDTO.setName("Российский рубль");
        currencyDTO.setAbbreviation("RUR");
        currencyDTO.setCode("810");
        when(objectMapper.readValue(eq(currenciesJson), any(TypeReference.class))).thenReturn(Arrays.asList(currencyDTO));

        doThrow(new RuntimeException("Database save failure")).when(currencyRepository).saveAll(anyList());

        CurrencyServiceException exception = assertThrows(CurrencyServiceException.class, () -> currencyService.fetchAndSaveCurrencies());
        assertEquals("Error saving currencies to database", exception.getMessage());
    }
}