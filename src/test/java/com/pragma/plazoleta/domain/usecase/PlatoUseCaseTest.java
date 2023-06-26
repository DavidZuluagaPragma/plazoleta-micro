package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.dto.*;
import com.pragma.plazoleta.domain.model.categoria.Categoria;
import com.pragma.plazoleta.domain.model.plato.Plato;
import com.pragma.plazoleta.domain.model.plato.gateway.PlatoRepository;
import com.pragma.plazoleta.domain.model.restaurante.Restaurante;
import com.pragma.plazoleta.domain.model.restaurante.gateway.RestauranteRepository;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import com.pragma.plazoleta.infrastructure.persistence.categoria.CategoriaData;
import com.pragma.plazoleta.infrastructure.persistence.plato.PlatoData;
import com.pragma.plazoleta.infrastructure.persistence.restaurante.RestauranteData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PlatoUseCaseTest {

    @InjectMocks
    PlatoUseCase platoUseCase;

    @Mock
    RestauranteUseCase restauranteUseCase;

    @Mock
    CategoriaUseCase categoriaUseCase;

    @Mock
    PlatoRepository platoRepository;

    @Mock
    RestauranteRepository restauranteRepository;

    @Test
    void crearPlato() {

        var restauranteEsperado = Restaurante.builder()
                .nombre("PAPANATAS")
                .direccion("LOS PATIOS")
                .telefono("+573202040834")
                .urlLogo("urlLogo")
                .nit("123456")
                .idPropietario(1)
                .build();

        var categoriaEsperada = Categoria.builder()
                .Id(1)
                .Nombre("COMIDA RAPIDA")
                .Descripcion("COMIDA RAPIDA")
                .build();

        var categoria = CategoriaData.builder()
                .Id(1)
                .Nombre("COMIDA RAPIDA")
                .Descripcion("COMIDA RAPIDA")
                .build();

        var restaurante = RestauranteData.builder()
                .nombre("PAPANATAS")
                .direccion("LOS PATIOS")
                .telefono("+573202040834")
                .urlLogo("urlLogo")
                .nit("123456")
                .idPropietario(1)
                .build();

        var platoNuevo = PlatoData.builder()
                .precio(100.0)
                .urlImagen("url")
                .nombre("PICADA")
                .descripcion("PLATO")
                .activo(Boolean.TRUE)
                .categoria(categoria)
                .restaurante(restaurante)
                .build();

        var platoEsperado = Plato.builder()
                .precio(100.0)
                .urlImagen("url")
                .nombre("PICADA")
                .descripcion("PLATO")
                .activo(Boolean.TRUE)
                .categoriaId(1)
                .restauranteId(1)
                .build();

        var platoDto = PlatoDto.builder()
                .precio(100.0)
                .urlImagen("url")
                .nombre("PICADA")
                .descripcion("PLATO")
                .activo(Boolean.TRUE)
                .categoriaId(1)
                .restauranteId(1)
                .usuarioId("1")
                .build();

        Mockito.when(restauranteUseCase.esPropetario("1")).thenReturn(Mono.just(Boolean.TRUE));
        Mockito.when(restauranteUseCase.existeRestaurante("1")).thenReturn(Mono.just(restauranteEsperado));
        Mockito.when(categoriaUseCase.conseguirCategoriaPorId("1")).thenReturn(Mono.just(categoriaEsperada));
        Mockito.when(platoRepository.crearPlato(platoNuevo)).thenReturn(Mono.just(platoEsperado));

        var result = platoUseCase.crearPlato(platoDto, platoDto.getUsuarioId());

        StepVerifier.create(result)
                .expectNext(platoEsperado)
                .expectComplete()
                .verify();


    }

    @Test
    void crearPlatoErrorNoEsPropetario() {

        var platoDto = PlatoDto.builder()
                .precio(100.0)
                .urlImagen("url")
                .nombre("PICADA")
                .descripcion("PLATO")
                .activo(Boolean.TRUE)
                .categoriaId(1)
                .restauranteId(1)
                .usuarioId("1")
                .build();

        Mockito.when(restauranteUseCase.esPropetario("1")).thenReturn(Mono.just(Boolean.FALSE));
        var result = platoUseCase.crearPlato(platoDto, platoDto.getUsuarioId());

        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.USUARIO_NO_PROPETARIO.getMessage())
                .verify();
    }

    @Test
    void editarPlato() {

        var categoria = CategoriaData.builder()
                .Id(1)
                .Nombre("COMIDA RAPIDA")
                .Descripcion("COMIDA RAPIDA")
                .build();

        var restaurante = RestauranteData.builder()
                .nombre("PAPANATAS")
                .direccion("LOS PATIOS")
                .telefono("+573202040834")
                .urlLogo("urlLogo")
                .nit("123456")
                .idPropietario(1)
                .build();

        var platoEncontrado = PlatoData.builder()
                .precio(100.0)
                .urlImagen("url")
                .nombre("PICADA")
                .descripcion("PLATO")
                .activo(Boolean.TRUE)
                .categoria(categoria)
                .restaurante(restaurante)
                .build();

        var platoDto = PlatoEditarDto.builder()
                .id(1)
                .descripcion("DESCIPCION")
                .precio(2000.0)
                .usuarioId("1")
                .build();

        var platoEditado = Plato.builder()
                .urlImagen("url")
                .nombre("PICADA")
                .activo(Boolean.TRUE)
                .categoriaId(categoria.getId())
                .restauranteId(restaurante.getId())
                .precio(platoDto.getPrecio())
                .descripcion(platoDto.getDescripcion())
                .build();

        Mockito.when(restauranteUseCase.esPropetario("1")).thenReturn(Mono.just(Boolean.TRUE));
        Mockito.when(platoRepository.encontrarPlatoPorId(1)).thenReturn(Mono.just(Optional.of(platoEncontrado)));
        Mockito.when(platoRepository.crearPlato(platoEncontrado.toBuilder()
                .precio(platoDto.getPrecio())
                .descripcion(platoDto.getDescripcion())
                .build())).thenReturn(Mono.just(platoEditado));

        var result = platoUseCase.editar(platoDto, platoDto.getUsuarioId());

        StepVerifier.create(result)
                .expectNext(platoEditado)
                .expectComplete()
                .verify();

    }

    @Test
    void editarPlatoErrorNoEsPropetario() {

        var platoDto = PlatoEditarDto.builder()
                .id(1)
                .descripcion("DESCIPCION")
                .precio(2000.0)
                .usuarioId("1")
                .build();

        Mockito.when(restauranteUseCase.esPropetario("1")).thenReturn(Mono.just(Boolean.FALSE));
        var result = platoUseCase.editar(platoDto, platoDto.getUsuarioId());

        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.USUARIO_NO_PROPETARIO.getMessage())
                .verify();
    }

    @Test
    void editarPlatoErrorIdEsNull() {
        var platoDto = PlatoEditarDto.builder()
                .precio(100.0)
                .descripcion("PLATO")
                .usuarioId("1")
                .build();

        Mockito.when(restauranteUseCase.esPropetario("1")).thenReturn(Mono.just(Boolean.TRUE));
        var result = platoUseCase.editar(platoDto, platoDto.getUsuarioId());

        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.EDITAR_PLATO_ERROR.getMessage())
                .verify();
    }

    @Test
    void editarPlatoErrorNoSeEncontroPlato() {
        var platoDto = PlatoEditarDto.builder()
                .id(1)
                .precio(100.0)
                .descripcion("PLATO")
                .usuarioId("1")
                .build();

        Mockito.when(restauranteUseCase.esPropetario("1")).thenReturn(Mono.just(Boolean.TRUE));
        Mockito.when(platoRepository.encontrarPlatoPorId(1)).thenReturn(Mono.just(Optional.empty()));
        var result = platoUseCase.editar(platoDto, platoDto.getUsuarioId());

        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.ERROR_BASE_DATOS_PLATO_NO_ENCONTRADO.getMessage())
                .verify();
    }

    @Test
    void conseguirTodosLosPlatosExitoso() {
        List<RestauranteData> restauranteDataList = new ArrayList<>();

        RestauranteData restaurante = RestauranteData.builder()
                .nombre("PAPANATAS")
                .direccion("LOS PATIOS")
                .telefono("+573202040834")
                .urlLogo("urlLogo")
                .nit("123456")
                .idPropietario(1)
                .build();

        restauranteDataList.add(restaurante);

        int numeroPagina = 0;
        int tamanoPagina = 10;
        PageRequest pageRequest = PageRequest.of(numeroPagina, tamanoPagina);


        List<PlatoData> platoDataLista = new ArrayList<>();

        CategoriaData categoria = CategoriaData.builder()
                .Id(1)
                .Nombre("COMIDA RAPIDA")
                .Descripcion("COMIDA RAPIDA")
                .build();

        var plato = Plato.builder()
                .precio(100.0)
                .urlImagen("url")
                .nombre("PICADA")
                .descripcion("PLATO")
                .activo(Boolean.TRUE)
                .categoriaId(1)
                .restauranteId(restaurante.getId())
                .build();

        var platoEncontrado = PlatoData.builder()
                .precio(100.0)
                .urlImagen("url")
                .nombre("PICADA")
                .descripcion("PLATO")
                .activo(Boolean.TRUE)
                .categoria(categoria)
                .restaurante(restaurante)
                .build();

        PlatoRespuestaDto platoRespuestaDto = PlatoRespuestaDto
                .builder()
                .platos(List.of(plato))
                .categoriaDto(CategoriaDto.builder()
                        .id(1)
                        .nombre(categoria.getNombre())
                        .descripcion(categoria.getDescripcion())
                        .build())
                .restauranteDto(RestauranteRespuestaDto.builder()
                        .nombre("PAPANATAS")
                        .urlLogo("urlLogo")
                        .build())
                .build();

        platoDataLista.add(platoEncontrado);

        Page<RestauranteData> restaurantePage = new PageImpl<>(restauranteDataList, pageRequest, restauranteDataList.size());


        com.pragma.plazoleta.domain.model.page.Page pageEsperado = com.pragma.plazoleta.domain.model.page.Page.builder()
                .contenido(List.of(platoRespuestaDto))
                .totalElementos(platoDataLista.stream().count())
                .build();


        Mockito.when(restauranteRepository.conseguirRestaurantes(1, 10)).thenReturn(Mono.just(restaurantePage));
        Mockito.when(platoRepository.encontrarPlatoPorRestaurante(restaurante.getId())).thenReturn(Flux.just(platoEncontrado));
        Mockito.when(platoRepository.listaTodosRestaurantes(restaurante.getId())).thenReturn(List.of(plato));

        var result = platoUseCase.conseguirTodosLosPlatos(1, 10);

        StepVerifier.create(result)
                .expectNext(pageEsperado)
                .expectComplete()
                .verify();
    }

}