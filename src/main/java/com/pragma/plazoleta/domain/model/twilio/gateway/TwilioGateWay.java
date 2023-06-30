package com.pragma.plazoleta.domain.model.twilio.gateway;

import com.pragma.plazoleta.aplication.dto.EnviarMensajeDto;
import com.pragma.plazoleta.aplication.dto.RespuestaMensajeDto;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

public interface TwilioGateWay {
    Mono<RespuestaMensajeDto> enviarMensaje(EnviarMensajeDto enviarMensajeDto, String token);
    Mono<String> validarCodigo(EnviarMensajeDto enviarMensajeDto, String token);
}
