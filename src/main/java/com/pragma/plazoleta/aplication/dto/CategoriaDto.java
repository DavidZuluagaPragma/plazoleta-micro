package com.pragma.plazoleta.aplication.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class CategoriaDto {
    private Integer id;
    @NonNull
    private String nombre;
    @NonNull
    private String descripcion;
}
