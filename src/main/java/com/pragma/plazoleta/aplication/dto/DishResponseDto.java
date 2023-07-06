package com.pragma.plazoleta.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class DishResponseDto {
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
}
