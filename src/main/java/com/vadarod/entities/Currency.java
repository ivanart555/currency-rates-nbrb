package com.vadarod.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Currency {
    private Long id;
    private String name;
    private String abbreviation;
    private String code;
}
