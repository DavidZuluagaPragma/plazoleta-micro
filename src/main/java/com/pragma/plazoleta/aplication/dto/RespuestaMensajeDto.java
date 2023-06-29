package com.pragma.plazoleta.aplication.dto;

import com.pragma.plazoleta.domain.model.twilio.TwilioEstado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RespuestaMensajeDto {
    private TwilioEstado estado;
    private String mensaje;
}
