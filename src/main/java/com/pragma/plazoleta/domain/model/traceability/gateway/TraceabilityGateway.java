package com.pragma.plazoleta.domain.model.traceability.gateway;

import com.pragma.plazoleta.aplication.dto.TraceabilityRequestDto;
import com.pragma.plazoleta.domain.model.traceability.Traceability;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public interface TraceabilityGateway {
    Mono<Traceability> createTraceabilityForOrder(TraceabilityRequestDto traceabilityRequestDto, String token);
}
