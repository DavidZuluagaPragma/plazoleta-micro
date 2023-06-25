package com.pragma.plazoleta.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class PlatoCambiarEstadoDto {
    @NonNull
    private Integer id;
    @NonNull
    private Boolean activo;
    @NonNull
    private String restauranteId;
}
