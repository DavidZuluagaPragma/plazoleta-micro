package com.pragma.plazoleta.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderDto {
    @NonNull
    private Integer clientId;
    @NonNull
    private Integer restaurantId;
    @NonNull
    private List<OrderPlateRequestDto> dishes;
}
