package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.mapper.DataMapper;
import com.pragma.plazoleta.domain.model.common.ValidationRestaurante;
import com.pragma.plazoleta.domain.model.restaurante.Restaurante;
import com.pragma.plazoleta.domain.model.restaurante.gateway.RestauranteRepository;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
                        }).onErrorResume(throwable -> Mono.error(new BusinessException(BusinessException.Type.USUARIO_NO_PROPETARIO))));
    }
}
