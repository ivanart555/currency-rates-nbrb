package com.vadarod.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CurrencyDTO {
    @JsonProperty("Cur_ID")
    private Long curId;

    @JsonProperty("Cur_Name")
    private String name;

    @JsonProperty("Cur_Abbreviation")
    private String abbreviation;

    @JsonProperty("Cur_Code")
    private String code;
}