package com.pragma.plazoleta.domain.model.plato.gateway;

import com.pragma.plazoleta.domain.model.plato.Plato;
import com.pragma.plazoleta.infrastructure.persistence.plato.PlatoData;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PlatoRepository {
    Mono<Plato> crearPlato(PlatoData platoData);
}
