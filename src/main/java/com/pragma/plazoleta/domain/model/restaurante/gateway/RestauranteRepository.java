package com.pragma.plazoleta.domain.model.restaurante.gateway;

import com.pragma.plazoleta.domain.model.restaurante.Restaurante;
import com.pragma.plazoleta.infrastructure.persistence.RestauranteData;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RestauranteRepository {
    Mono<Boolean> esPropetario(String usuarioId);
    Mono<Restaurante> creaRestaurante(RestauranteData restauranteData);
}
