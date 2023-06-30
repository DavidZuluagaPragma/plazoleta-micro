package com.pragma.plazoleta.infrastructure.web_service;

import com.pragma.plazoleta.aplication.config.HeaderRequest;
import com.pragma.plazoleta.aplication.dto.EnviarMensajeDto;
import com.pragma.plazoleta.aplication.dto.RespuestaMensajeDto;
import com.pragma.plazoleta.domain.model.common.Utils;
import com.pragma.plazoleta.domain.model.twilio.gateway.TwilioGateWay;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class TwilioService implements TwilioGateWay {

    @Autowired
    private HeaderRequest header;

    @Value("${urlBaseTwilio}")
    private String urlBase;

    @Override
    public Mono<RespuestaMensajeDto> enviarMensaje(EnviarMensajeDto enviarMensajeDto, String token) {
        try {
            return WebClient.builder()
                    .baseUrl(urlBase)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build()
                    .post()
                    .uri("/sendOTP" )
                    .header(Utils.ACCEPT, header.headers().getAccept())
                    .header(Utils.CONTENT_TYPE, header.headers().getContentType())
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .body(Mono.just(enviarMensajeDto), EnviarMensajeDto.class)
                    .retrieve()
                    .bodyToMono(RespuestaMensajeDto.class)
                    .onErrorResume(e -> {
                        var responseException = (WebClientResponseException) e;
                        if (e instanceof WebClientResponseException &&
                                (responseException.getStatusCode().is4xxClientError())) {
                            return Mono.error(new BusinessException(BusinessException.Type.ERROR_ENVIAR_MENSAJE));
                        }
                        return Mono.error(new BusinessException(BusinessException.Type.ERROR_ENVIAR_MENSAJE));
                    });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<String> validarCodigo(EnviarMensajeDto enviarMensajeDto, String token) {
        try {
            return WebClient.builder()
                    .baseUrl(urlBase)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build()
                    .post()
                    .uri("/validateOTP" )
                    .header(Utils.ACCEPT, header.headers().getAccept())
                    .header(Utils.CONTENT_TYPE, header.headers().getContentType())
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .body(Mono.just(enviarMensajeDto), EnviarMensajeDto.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(e -> {
                        var responseException = (WebClientResponseException) e;
                        if (e instanceof WebClientResponseException &&
                                (responseException.getStatusCode().is4xxClientError())) {
                            return Mono.error(new BusinessException(BusinessException.Type.ERROR_ENVIAR_MENSAJE));
                        }
                        return Mono.error(new BusinessException(BusinessException.Type.ERROR_ENVIAR_MENSAJE));
                    });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
