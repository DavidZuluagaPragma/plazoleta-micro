package com.pragma.plazoleta.infrastructure.web_service;

import com.pragma.plazoleta.aplication.config.HeaderRequest;
import com.pragma.plazoleta.aplication.dto.EnviarMensajeDto;
import com.pragma.plazoleta.aplication.dto.RespuestaMensajeDto;
import com.pragma.plazoleta.aplication.dto.TraceabilityRequestDto;
import com.pragma.plazoleta.domain.model.common.Utils;
import com.pragma.plazoleta.domain.model.traceability.Traceability;
import com.pragma.plazoleta.domain.model.traceability.gateway.TraceabilityGateway;
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
public class TraceabilityService implements TraceabilityGateway {

    @Autowired
    private HeaderRequest header;

    @Value("${urlBaseTraceability}")
    private String urlBase;

    @Override
    public Mono<Traceability> createTraceabilityForOrder(TraceabilityRequestDto traceabilityRequestDto, String token) {
        try {
            return WebClient.builder()
                    .baseUrl(urlBase)
                    .build()
                    .post()
                    .header(Utils.ACCEPT, header.headers().getAccept())
                    .header(Utils.CONTENT_TYPE, header.headers().getContentType())
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .body(Mono.just(traceabilityRequestDto), TraceabilityRequestDto.class)
                    .retrieve()
                    .bodyToMono(Traceability.class)
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
