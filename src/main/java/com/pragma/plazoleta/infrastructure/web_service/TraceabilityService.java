package com.pragma.plazoleta.infrastructure.web_service;

import com.pragma.plazoleta.aplication.config.HeaderRequest;
import com.pragma.plazoleta.aplication.dto.TraceabilityRequestDto;
import com.pragma.plazoleta.domain.model.common.Utils;
import com.pragma.plazoleta.domain.model.traceability.Traceability;
import com.pragma.plazoleta.domain.model.traceability.gateway.TraceabilityGateway;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
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
                            return Mono.error(new BusinessException(BusinessException.Type.ERROR_SENDING_MESSAGE));
                        }
                        return Mono.error(new BusinessException(BusinessException.Type.ERROR_SENDING_MESSAGE));
                    });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Flux<Traceability> getAllCompletedTraceability(String token) {
         try {
            return WebClient.builder()
                    .baseUrl(urlBase)
                    .build()
                    .get()
                    .header(Utils.ACCEPT, header.headers().getAccept())
                    .header(Utils.CONTENT_TYPE, header.headers().getContentType())
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToFlux(Traceability.class)
                    .onErrorResume(e -> {
                        var responseException = (WebClientResponseException) e;
                        if (e instanceof WebClientResponseException &&
                                (responseException.getStatusCode().is4xxClientError())) {
                            return Flux.error(new BusinessException(BusinessException.Type.ERROR_GET_TRACEABILITY));
                        }
                        return Flux.error(new BusinessException(BusinessException.Type.ERROR_GET_TRACEABILITY));
                    });
        } catch (Exception e) {
            return Flux.error(e);
        }
    }
}
