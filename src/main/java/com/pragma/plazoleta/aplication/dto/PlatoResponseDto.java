package com.pragma.plazoleta.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class PlatoResponseDto {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer cantidad;
}
