package com.pragma.plazoleta.domain.model.restaurante;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Restaurante {
    private Integer id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String urlLogo;
    private String nit;
    private Integer idPropietario;

    public boolean esNombreValido() {
        return !nombre.matches("\\d+");
    }
}
