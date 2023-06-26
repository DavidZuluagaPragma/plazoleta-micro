package com.pragma.plazoleta.domain.model.plato.gateway;

import com.pragma.plazoleta.domain.model.plato.Plato;
import com.pragma.plazoleta.infrastructure.persistence.plato.PlatoData;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatoRepository {
    Mono<Plato> crearPlato(PlatoData platoData);
    Mono<Optional<PlatoData>> encontrarPlatoPorId(Integer platoId);
    Flux<PlatoData> encontrarPlatoPorRestaurante(Integer platoId);
    List<Plato> listaTodosRestaurantes(Integer platoId);
}
