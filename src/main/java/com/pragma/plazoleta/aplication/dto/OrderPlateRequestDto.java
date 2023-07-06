package com.pragma.plazoleta.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderPlateRequestDto {
    @NonNull
    private Integer dishId;
    @NonNull
    private Integer amount;
}
