package com.pragma.plazoleta.domain.model.plato;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Plato {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String urlImagen;
    private Boolean activo;
    private Integer restauranteId;
    private Integer categoriaId;
}
