package com.pragma.plazoleta.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class PedidoPlatoDto {
    @NonNull
    private Integer platoId;
    @NonNull
    private Integer cantidad;
}
