package com.pragma.plazoleta.infrastructure.persistence.plato;

import com.pragma.plazoleta.aplication.mapper.DataMapper;
import com.pragma.plazoleta.domain.model.plato.Plato;
import com.pragma.plazoleta.domain.model.plato.gateway.PlatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class PlatoAdapterRepository implements PlatoRepository {

    @Autowired
    PlatoDataRepository repository;

    @Override
    public Mono<Plato> crearPlato(PlatoData platoData) {
        return Mono.fromCallable(() -> repository.save(platoData))
                .map(DataMapper::convertirPlatoDataAPlato);
    }

    @Override
    public Mono<Optional<PlatoData>> encontrarPlatoPorId(Integer platoId) {
        return Mono.fromCallable(() -> repository.findById(platoId));
    }
}
