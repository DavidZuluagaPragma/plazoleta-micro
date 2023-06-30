package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.dto.PlatoCambiarEstadoDto;
import com.pragma.plazoleta.aplication.dto.PlatoDto;
import com.pragma.plazoleta.aplication.dto.PlatoEditarDto;
import com.pragma.plazoleta.aplication.dto.PlatoRespuestaDto;
import com.pragma.plazoleta.aplication.mapper.DataMapper;
import com.pragma.plazoleta.domain.model.page.Page;
import com.pragma.plazoleta.domain.model.plato.Plato;
import com.pragma.plazoleta.domain.model.plato.gateway.PlatoRepository;
import com.pragma.plazoleta.domain.model.restaurante.gateway.RestauranteRepository;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import com.pragma.plazoleta.infrastructure.persistence.plato.PlatoData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class PlatoUseCase {

    @Autowired
    PlatoRepository platoRepository;

    @Autowired
    RestauranteUseCase restauranteUseCase;

    @Autowired
    RestauranteRepository restauranteRepository;

    @Autowired
    CategoriaUseCase categoriaUseCase;

    public Mono<Plato> crearPlato(PlatoDto platoDto, String usuarioId, String token) {
        return restauranteUseCase.esPropetario(usuarioId, token)
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


    public Mono<Plato> editar(PlatoEditarDto platoDto, String usuarioId, String token) {
        return restauranteUseCase.esPropetario(usuarioId, token)
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

    public Mono<Plato> cambiarEstadoPlato(PlatoCambiarEstadoDto platoDto, String usuarioId) {
        return restauranteUseCase.existeRestaurante(platoDto.getRestauranteId())
                .flatMap(restaurante -> {
                    if (restaurante.getIdPropietario() != Integer.parseInt(usuarioId)) {
                        return Mono.error(new BusinessException(BusinessException.Type.PLATO_NO_ES_DEL_PROPETARIO));
                    }
                    return platoRepository.encontrarPlatoPorId(platoDto.getId())
                            .flatMap(platoData -> {
                                if (!platoData.isPresent()) {
                                    return Mono.error(new BusinessException(BusinessException.Type.ERROR_BASE_DATOS_PLATO_NO_ENCONTRADO));
                                }
                                return platoRepository.crearPlato(platoData.get()
                                        .toBuilder()
                                        .activo(platoDto.getActivo())
                                        .build());
                            });
                });
    }

    public Mono<Page<PlatoRespuestaDto>> conseguirTodosLosPlatos(int numeroPagina, int tamanoPagina) {
       return restauranteRepository.conseguirRestaurantes(numeroPagina, tamanoPagina)
                .flatMapMany(restaurantesPage -> Flux.fromIterable(restaurantesPage.getContent()))
                .flatMap(restauranteData -> platoRepository.encontrarPlatoPorRestaurante(restauranteData.getId())
                        .map(DataMapper::convertirPlatoDataAPlatoRespuestaDto)
                        .map(platoRespuestaDto -> platoRespuestaDto.toBuilder()
                                .platos(platoRepository.listaTodosRestaurantes(restauranteData.getId()))
                                .build())).collectList()
                .map(platoRespuestaDtos -> new Page<>(platoRespuestaDtos, platoRespuestaDtos.stream().count()));
    }

}
