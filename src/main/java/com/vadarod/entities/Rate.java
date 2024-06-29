package com.vadarod.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vadarod.config.CurrencyDeserializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "currency_id", referencedColumnName = "curId")
    @JsonProperty("Cur_ID")
    @JsonDeserialize(using = CurrencyDeserializer.class)
    private Currency currency;

    @JsonProperty("Date")
    private LocalDate date;

    @JsonProperty("Cur_OfficialRate")
    private BigDecimal rate;

    @JsonProperty("Cur_Scale")
    private int scale;
}
