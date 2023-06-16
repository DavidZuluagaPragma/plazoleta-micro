package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.dto.PlatoDto;
import com.pragma.plazoleta.domain.model.categoria.Categoria;
import com.pragma.plazoleta.domain.model.categoria.gateway.CategoriaRepository;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
}