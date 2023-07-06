package com.pragma.plazoleta.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class DishEditDto {
    private Integer id;
    @NonNull
    private String description;
    @NonNull
    private Double price;
}
