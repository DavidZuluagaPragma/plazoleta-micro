package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.dto.PlatoDto;
import com.pragma.plazoleta.aplication.dto.PlatoEditarDto;
import com.pragma.plazoleta.aplication.mapper.DataMapper;
import com.pragma.plazoleta.domain.model.plato.Plato;
import com.pragma.plazoleta.domain.model.plato.gateway.PlatoRepository;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import com.pragma.plazoleta.infrastructure.persistence.plato.PlatoData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class PlatoUseCase {

    @Autowired
    PlatoRepository platoRepository;

    @Autowired
    RestauranteUseCase restauranteUseCase;

    @Autowired
    CategoriaUseCase categoriaUseCase;

    public Mono<Plato> crearPlato(PlatoDto platoDto, String usuarioId) {
        return restauranteUseCase.esPropetario(usuarioId)
                .flatMap(esPropetario -> {
                    if (!esPropetario) {
                        return Mono.error(new BusinessException(BusinessException.Type.USUARIO_NO_PROPETARIO));
                    }
                    return restauranteUseCase.existeRestaurante(platoDto.getRestauranteId().toString())
                            .flatMap(restaurante -> categoriaUseCase.conseguirCategoriaPorId(platoDto.getCategoriaId().toString())
                                    .flatMap(categoria -> platoRepository.crearPlato(PlatoData.builder()
                                            .nombre(platoDto.getNombre())
                                            .descripcion(platoDto.getDescripcion())
                                            .urlImagen(platoDto.getUrlImagen())
                                            .precio(platoDto.getPrecio())
                                            .activo(Boolean.TRUE)
                                            .categoria(DataMapper.convertirCategoriaACategoriaData(categoria))
                                            .restaurante(DataMapper.convertirRestauranteARestauranteData(restaurante))
                                            .build())));
                });
    }


    public Mono<Plato> editar(PlatoEditarDto platoDto, String usuarioId) {
        return restauranteUseCase.esPropetario(usuarioId)
                .flatMap(esPropetario -> {
                    if (Boolean.FALSE.equals(esPropetario)) {
                        return Mono.error(new BusinessException(BusinessException.Type.USUARIO_NO_PROPETARIO));
                    }
                    if (platoDto.getId() == null) {
                        return Mono.error(new BusinessException(BusinessException.Type.EDITAR_PLATO_ERROR));
                    }
                    return platoRepository.encontrarPlatoPorId(platoDto.getId())
                            .flatMap(platoData -> {
                                if (!platoData.isPresent()) {
                                    return Mono.error(new BusinessException(BusinessException.Type.ERROR_BASE_DATOS_PLATO_NO_ENCONTRADO));
                                }
                                return platoRepository.crearPlato(platoData.get()
                                        .toBuilder()
                                        .precio(platoDto.getPrecio())
                                        .descripcion(platoDto.getDescripcion())
                                        .build());
                            });
                });
    }
}
