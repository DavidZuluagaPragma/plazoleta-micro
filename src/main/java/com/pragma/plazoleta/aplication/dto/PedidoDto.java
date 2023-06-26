package com.pragma.plazoleta.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class PedidoDto {
    @NonNull
    private Integer clienteId;
    @NonNull
    private Integer chefId;
    @NonNull
    private Integer restauranteId;
    @NonNull
    private List<PedidoPlatoDto> platos;
}
