package com.pragma.plazoleta.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class PlatoEditarDto {
    private Integer id;
    @NonNull
    private String descripcion;
    @NonNull
    private Double precio;
    @NonNull
    private String usuarioId;
}
