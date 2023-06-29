package com.pragma.plazoleta.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class EnviarMensajeDto {
    private String numeroDestino;
    private String usuario;
    private String tiempoContra;
}
