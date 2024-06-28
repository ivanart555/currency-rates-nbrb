package com.vadarod.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rate {
    private Long id;
    private Currency currency;
    private LocalDate date;
    private BigDecimal rate;
    private int scale;
}
