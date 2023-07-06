package com.pragma.plazoleta.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class DishChangeStatusDto {
    @NonNull
    private Integer id;
    @NonNull
    private Boolean active;
    @NonNull
    private String restaurantId;
}
