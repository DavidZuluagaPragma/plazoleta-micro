package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.model.categoria.Categoria;
import com.pragma.plazoleta.domain.model.categoria.gateway.CategoriaRepository;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import com.pragma.plazoleta.infrastructure.persistence.categoria.CategoriaData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoriaUseCaseTest {

    @InjectMocks
    CategoriaUseCase categoriaUseCase;

    @Mock
    CategoriaRepository categoriaRepository;

    @Test
    void crearCategoria() {
        var categoriaEsperada = Categoria.builder()
                .Nombre("COMIDA RAPIDA")
                .Descripcion("COMIDA RAPIDA")
                .build();

        var categoria = CategoriaData.builder()
                .Nombre("COMIDA RAPIDA")
                .Descripcion("COMIDA RAPIDA")
                .build();

        Mockito.when(categoriaRepository.crearCategoria(categoria)).thenReturn(Mono.just(categoriaEsperada));

        var result =  categoriaUseCase.crearCategoria(categoriaEsperada);

        StepVerifier.create(result)
                .expectNext(categoriaEsperada)
                .expectComplete()
                .verify();
    }

    @Test
    void conseguirCategoriaPorId() {

        var categoria = CategoriaData.builder()
                .Nombre("COMIDA RAPIDA")
                .Descripcion("COMIDA RAPIDA")
                .build();

        var categoriaEsperada = Categoria.builder()
                .Nombre("COMIDA RAPIDA")
                .Descripcion("COMIDA RAPIDA")
                .build();

        Mockito.when(categoriaRepository.conseguirCategoriaPorId("1")).thenReturn(Mono.just(Optional.of(categoria)));

        var result =  categoriaUseCase.conseguirCategoriaPorId("1");

        StepVerifier.create(result)
                .expectNext(categoriaEsperada)
                .expectComplete()
                .verify();
    }

    @Test
    void conseguirCategoriaPorIdError() {

        Mockito.when(categoriaRepository.conseguirCategoriaPorId("1")).thenReturn(Mono.just(Optional.empty()));

        var result =  categoriaUseCase.conseguirCategoriaPorId("1");

        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.ERROR_BASE_DATOS_CATEGORIA_NO_ENCONTRADA.getMessage())
                .verify();
    }
}