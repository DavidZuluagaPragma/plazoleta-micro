package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.dto.RestauranteRespuestaDto;
import com.pragma.plazoleta.aplication.mapper.DataMapper;
import com.pragma.plazoleta.aplication.mapper.DtoMapper;
import com.pragma.plazoleta.domain.model.common.ValidationRestaurante;
import com.pragma.plazoleta.domain.model.page.Page;
import com.pragma.plazoleta.domain.model.restaurante.Restaurante;
import com.pragma.plazoleta.domain.model.restaurante.gateway.RestauranteRepository;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RestauranteUseCase {

    @Autowired
    RestauranteRepository restauranteRepository;

    public Mono<Restaurante> crearRestaurante(Restaurante restaurante) {
        return ValidationRestaurante.validarRestauranteDto(restaurante)
                .flatMap(restauranteValidado -> restauranteRepository.esPropetario(restaurante.getIdPropietario().toString())
                        .flatMap(esPropetario -> {
                            if (!esPropetario) {
                                return Mono.error(new BusinessException(BusinessException.Type.USUARIO_NO_PROPETARIO));
                            }
                            return restauranteRepository.creaRestaurante(DataMapper.convertirRestauranteARestauranteData(restaurante));
                        }));
    }

    public Mono<Boolean> esPropetario(String usuarioId) {
        return restauranteRepository.esPropetario(usuarioId);
    }

    public Mono<Restaurante> existeRestaurante(String restauranteId) {
        return restauranteRepository.existeRestaurante(restauranteId)
                .flatMap(restauranteData -> {
                    if (!restauranteData.isPresent()) {
                        return Mono.error(new BusinessException(BusinessException.Type.ERROR_BASE_DATOS_RESTAURANTE_NO_ENCONTRADA));
                    }
                    return Mono.just(DataMapper.convertirRestauranteDataARestaurante(restauranteData.get()));
                });
    }

    public Mono<Page<RestauranteRespuestaDto>> conseguirRestaurantes(int numeroPagina, int tamanoPagina) {
        return restauranteRepository.conseguirRestaurantes(numeroPagina, tamanoPagina)
                .map(restauranteData -> restauranteData.stream()
                        .map(DtoMapper::convertirRestauranteDataARestauranteRespuesta)
                        .collect(Collectors.toList()))
                .map(restauranteData -> new Page<>(restauranteData, restauranteData.stream().count()))
                .onErrorResume(Mono::error);
    }
}
