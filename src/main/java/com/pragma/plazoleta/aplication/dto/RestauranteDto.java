package com.pragma.plazoleta.aplication.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class RestauranteDto {
    private Integer id;
    @NonNull
    private String nombre;
    @NonNull
    private String direccion;
    @NonNull
    private String telefono;
    @NonNull
    private String urlLogo;
    @NonNull
    private String nit;
    @NonNull
    private Integer idPropietario;
}
