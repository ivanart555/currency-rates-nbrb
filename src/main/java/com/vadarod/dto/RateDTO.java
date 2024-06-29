package com.vadarod.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RateDTO {
    @JsonProperty("Cur_ID")
    private Long curId;

    @JsonProperty("Date")
    private LocalDate date;

    @JsonProperty("Cur_OfficialRate")
    private BigDecimal rate;

    @JsonProperty("Cur_Scale")
    private int scale;

    @JsonProperty("Cur_Abbreviation")
    private String abbreviation;

    @JsonProperty("Cur_Name")
    private String name;
}