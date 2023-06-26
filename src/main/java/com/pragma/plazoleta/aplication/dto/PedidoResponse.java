package com.pragma.plazoleta.aplication.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class PedidoResponse {
    private String estado;
    private Date fecha;
}
