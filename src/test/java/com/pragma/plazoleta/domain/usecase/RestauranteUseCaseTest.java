package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.dto.RestauranteRespuestaDto;
import com.pragma.plazoleta.domain.model.restaurante.Restaurante;
import com.pragma.plazoleta.domain.model.restaurante.gateway.RestauranteRepository;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class RestauranteUseCaseTest {

    @Mock
    RestauranteRepository restauranteRepository;

    @InjectMocks
    RestauranteUseCase restauranteUseCase;

    @Test
    void crearRestaurante() {
        var restaurante = RestauranteData.builder()
                .nombre("PAPANATAS")
                .direccion("LOS PATIOS")
                .telefono("+573202040834")
                .urlLogo("urlLogo")
                .nit("123456")
                .idPropietario(1)
                .build();

        var restauranteEsperado = Restaurante.builder()
                .nombre("PAPANATAS")
                .direccion("LOS PATIOS")
                .telefono("+573202040834")
                .urlLogo("urlLogo")
                .nit("123456")
                .idPropietario(1)
                .build();

        Mockito.when(restauranteRepository.esPropetario(String.valueOf(restaurante.getIdPropietario()))).thenReturn(Mono.just(Boolean.TRUE));
        Mockito.when(restauranteRepository.creaRestaurante(restaurante)).thenReturn(Mono.just(restauranteEsperado));

        var result = restauranteUseCase.crearRestaurante(restauranteEsperado);

        StepVerifier.create(result)
                .expectNext(restauranteEsperado)
                .expectComplete()
                .verify();

    }

    @Test
    void crearRestauranteErrorPropetario() {
        var restaurante = RestauranteData.builder()
                .nombre("PAPANATAS")
                .direccion("LOS PATIOS")
                .telefono("+573202040834")
                .urlLogo("urlLogo")
                .nit("123456")
                .idPropietario(1)
                .build();

        var restauranteEsperado = Restaurante.builder()
                .nombre("PAPANATAS")
                .direccion("LOS PATIOS")
                .telefono("+573202040834")
                .urlLogo("urlLogo")
                .nit("123456")
                .idPropietario(1)
                .build();

        Mockito.when(restauranteRepository.esPropetario(String.valueOf(restaurante.getIdPropietario()))).thenReturn(Mono.just(Boolean.FALSE));

        var result = restauranteUseCase.crearRestaurante(restauranteEsperado);

        StepVerifier.create(result)
                .expectErrorMessage("El usuario no es rol propetario")
                .verify();
    }

    @Test
    void crearRestauranteErrorTelefono() {
        var restauranteEsperado = Restaurante.builder()
                .nombre("PAPANATAS")
                .direccion("LOS PATIOS")
                .telefono("telefono")
                .urlLogo("urlLogo")
                .nit("123456")
                .idPropietario(1)
                .build();

        var result = restauranteUseCase.crearRestaurante(restauranteEsperado);

        StepVerifier.create(result)
                .expectErrorMessage("El telefono solo puede ser numerico")
                .verify();

    }

    @Test
    void esPropetarioExitoso(){
        Mockito.when(restauranteRepository.esPropetario("1")).thenReturn(Mono.just(Boolean.TRUE));

        var result = restauranteUseCase.esPropetario("1");

        StepVerifier.create(result)
                .expectNext(Boolean.TRUE)
                .expectComplete()
                .verify();
    }

    @Test
    void existeRestaurante(){

        var restaurante = RestauranteData.builder()
                .nombre("PAPANATAS")
                .direccion("LOS PATIOS")
                .telefono("+573202040834")
                .urlLogo("urlLogo")
                .nit("123456")
                .idPropietario(1)
                .build();

        var restauranteEsperado = Restaurante.builder()
                .nombre("PAPANATAS")
                .direccion("LOS PATIOS")
                .telefono("+573202040834")
                .urlLogo("urlLogo")
                .nit("123456")
                .idPropietario(1)
                .build();

        Mockito.when(restauranteRepository.existeRestaurante("1")).thenReturn(Mono.just(Optional.of(restaurante)));

        var result = restauranteUseCase.existeRestaurante("1");

        StepVerifier.create(result)
                .expectNext(restauranteEsperado)
                .expectComplete()
                .verify();

    }

    @Test
    void existeRestauranteErrorDatoVacio() {
        Mockito.when(restauranteRepository.existeRestaurante("1")).thenReturn(Mono.just(Optional.empty()));

        var result = restauranteUseCase.existeRestaurante("1");

        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.ERROR_BASE_DATOS_RESTAURANTE_NO_ENCONTRADA.getMessage())
                .verify();

    }

    @Test
    void conseguirRestaurantesExitoso() {

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

        Page<RestauranteData> restaurantePage = new PageImpl<>(restauranteDataList, pageRequest, restauranteDataList.size());

        List<RestauranteRespuestaDto> restauranteDataEsperado = new ArrayList<>();

        RestauranteRespuestaDto restauranteRespuestaDto = RestauranteRespuestaDto.builder()
                .nombre("PAPANATAS")
                .urlLogo("urlLogo")
                .build();

        restauranteDataEsperado.add(restauranteRespuestaDto);

        com.pragma.plazoleta.domain.model.page.Page pageEsperado = com.pragma.plazoleta.domain.model.page.Page.builder()
                .contenido(Arrays.asList(restauranteDataEsperado.toArray()))
                .totalElementos(restauranteDataEsperado.stream().count())
                .build();

        Mockito.when(restauranteRepository.conseguirRestaurantes(1, 10)).thenReturn(Mono.just(restaurantePage));

        var result = restauranteUseCase.conseguirRestaurantes(1, 10);

        StepVerifier.create(result)
                .expectNext(pageEsperado)
                .expectComplete()
                .verify();

    }

}