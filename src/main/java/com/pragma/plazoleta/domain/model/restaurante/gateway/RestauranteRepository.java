package com.pragma.plazoleta.domain.model.restaurante.gateway;

import com.pragma.plazoleta.aplication.dto.RestauranteRespuestaDto;
import com.pragma.plazoleta.domain.model.restaurante.Restaurante;
import com.pragma.plazoleta.infrastructure.persistence.restaurante.RestauranteData;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface RestauranteRepository {
    Mono<Boolean> esPropetario(String usuarioId);
    Mono<Restaurante> creaRestaurante(RestauranteData restauranteData);
    Mono<Optional<RestauranteData>> existeRestaurante (String restauranteId);
    Mono<Page<RestauranteData>> conseguirRestaurantes(int numeroPagina, int tamanoPagina);
}
