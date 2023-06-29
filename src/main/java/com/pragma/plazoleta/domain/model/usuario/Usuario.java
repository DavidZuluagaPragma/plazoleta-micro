package com.pragma.plazoleta.domain.model.usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Usuario implements Serializable {
    private Integer id;
    private String nombre;
    private String apellido;
    private String numeroDocumento;
    private String celular;
    private String fechaNacimiento;
    private String correo;
}
