package com.pragma.plazoleta.domain.model.categoria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Categoria {
    private Integer Id;
    private String Nombre;
    private String Descripcion;
}
