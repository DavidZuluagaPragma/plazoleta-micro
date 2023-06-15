package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.model.restaurante.Restaurante;
import com.pragma.plazoleta.domain.model.restaurante.gateway.RestauranteRepository;
import com.pragma.plazoleta.infrastructure.persistence.RestauranteData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

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

}