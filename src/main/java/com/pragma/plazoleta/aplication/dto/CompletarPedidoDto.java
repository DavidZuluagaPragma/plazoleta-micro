package com.pragma.plazoleta.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class CompletarPedidoDto {
    public Integer pedidoId;
    public String codigo;
}
