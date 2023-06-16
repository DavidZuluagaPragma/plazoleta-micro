package com.pragma.plazoleta.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class PlatoDto {
    private Integer id;
    @NonNull
    private String nombre;
    @NonNull
    private String descripcion;
    @NonNull
    private Double precio;
    @NonNull
    private String urlImagen;
    private Boolean activo;
    @NonNull
    private Integer restauranteId;
    @NonNull
    private Integer categoriaId;
    @NonNull
    private String usuarioId;
}
