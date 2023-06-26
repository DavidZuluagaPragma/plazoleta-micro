package com.pragma.plazoleta.domain.model.pedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Pedido {
    private Integer id;
    private Integer clienteId;
    private Integer chefId;
    private Integer restauranteId;
    private String estado;
    private Date fecha;
}
