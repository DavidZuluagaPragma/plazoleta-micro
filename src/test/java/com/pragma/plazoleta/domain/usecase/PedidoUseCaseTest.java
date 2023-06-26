package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.dto.PedidoDto;
import com.pragma.plazoleta.aplication.dto.PedidoPlatoDto;
import com.pragma.plazoleta.aplication.dto.PedidoResponse;
import com.pragma.plazoleta.domain.model.pedido.Pedido;
import com.pragma.plazoleta.domain.model.pedido.gateway.PedidoGateWay;
import com.pragma.plazoleta.domain.model.pedido_plato.PedidoPlato;
import com.pragma.plazoleta.domain.model.pedido_plato.gateway.PedidoPlatoGateWay;
import com.pragma.plazoleta.domain.model.plato.gateway.PlatoRepository;
import com.pragma.plazoleta.domain.model.restaurante.gateway.RestauranteRepository;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import com.pragma.plazoleta.infrastructure.persistence.categoria.CategoriaData;
import com.pragma.plazoleta.infrastructure.persistence.plato.PlatoData;
import com.pragma.plazoleta.infrastructure.persistence.restaurante.RestauranteData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PedidoUseCaseTest {

    @InjectMocks
    PedidoUseCase useCase;

    @Mock
    PedidoGateWay pedidoGateWay;

    @Mock
    PedidoPlatoGateWay pedidoPlatoGateWay;

    @Mock
    PlatoRepository platoRepository;

    @Mock
    RestauranteRepository restauranteRepository;


    @Test
    void crearPedido() {

        CategoriaData categoria = CategoriaData.builder()
                .Id(1)
                .Nombre("COMIDA RAPIDA")
                .Descripcion("COMIDA RAPIDA")
                .build();

        RestauranteData restaurante = RestauranteData.builder()
                .id(1)
                .nombre("PAPANATAS")
                .direccion("LOS PATIOS")
                .telefono("+573202040834")
                .urlLogo("urlLogo")
                .nit("123456")
                .idPropietario(1)
                .build();

        PlatoData platoData = PlatoData.builder()
                .precio(100.0)
                .urlImagen("url")
                .nombre("PICADA")
                .descripcion("PLATO")
                .activo(Boolean.TRUE)
                .categoria(categoria)
                .restaurante(restaurante)
                .build();

        PedidoPlatoDto pedidoPlatoDto = PedidoPlatoDto.builder()
                .platoId(1)
                .cantidad(1)
                .build();

        PedidoPlato pedidoPlato = PedidoPlato.builder()
                .pedidoId(1)
                .platoId(pedidoPlatoDto.getPlatoId())
                .cantidad(pedidoPlatoDto.getCantidad())
                .build();


        List<PedidoPlatoDto> pedidoPlatoDtos =  new ArrayList<>();

        pedidoPlatoDtos.add(pedidoPlatoDto);

        PedidoDto pedidoDto = PedidoDto.builder()
                .chefId(1)
                .restauranteId(1)
                .clienteId(2)
                .platos(pedidoPlatoDtos)
                .build();

        Pedido pedido =  Pedido.builder()
                .estado("PENDIENTE")
                .chefId(pedidoDto.getChefId())
                .clienteId(pedidoDto.getClienteId())
                .fecha(new Date())
                .restauranteId(pedidoDto.getRestauranteId())
                .build();

        PedidoResponse pedidoResponse = PedidoResponse.builder()
                .estado("PENDIENTE")
                .fecha(pedido.getFecha())
                .build();

        Mockito.when(pedidoGateWay.tienePedidoActivo(2)).thenReturn(Mono.just(Boolean.FALSE));
        Mockito.when(pedidoGateWay.maxId()).thenReturn(Mono.just(1));
        Mockito.when(platoRepository.encontrarPlatoPorId(pedidoPlatoDto.getPlatoId())).thenReturn(Mono.just(Optional.of(platoData)));
        Mockito.when(restauranteRepository.existeRestaurante(platoData.getRestaurante().getId().toString())).thenReturn(Mono.just(Optional.of(restaurante)));
        Mockito.when(pedidoPlatoGateWay.crearPedidoPlato(pedidoPlato)).thenReturn(Mono.just(pedidoPlato));
        Mockito.when(pedidoGateWay.crearPedido(pedido)).thenReturn(Mono.just(pedido));

        var result = useCase.crearPedido(pedidoDto);

        StepVerifier.create(result)
                .expectNext(pedidoResponse)
                .expectComplete();
    }

    @Test
    void crearPedidoErrorClienteNoTienePlato() {

        PedidoDto pedidoDto = PedidoDto.builder()
                .chefId(1)
                .restauranteId(1)
                .clienteId(2)
                .platos(new ArrayList<>())
                .build();

        var result = useCase.crearPedido(pedidoDto);

        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.NO_TIENE_PLATOS.getMessage())
                .verify();
    }

    @Test
    void crearPedidoErrorClienteTienePedido() {
        Mockito.when(pedidoGateWay.tienePedidoActivo(2)).thenReturn(Mono.just(Boolean.TRUE));

        PedidoPlatoDto pedidoPlatoDto = PedidoPlatoDto.builder()
                .platoId(1)
                .cantidad(1)
                .build();

        List<PedidoPlatoDto> pedidoPlatoDtos =  new ArrayList<>();

        pedidoPlatoDtos.add(pedidoPlatoDto);

        PedidoDto pedidoDto = PedidoDto.builder()
                .chefId(1)
                .restauranteId(1)
                .clienteId(2)
                .platos(pedidoPlatoDtos)
                .build();

        var result = useCase.crearPedido(pedidoDto);

        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.PEDIDO_ACTIVO.getMessage())
                .verify();
    }

    @Test
    void crearPedidoErrorNoExisteRestaurante() {

        PedidoPlatoDto pedidoPlatoDto = PedidoPlatoDto.builder()
                .platoId(1)
                .cantidad(1)
                .build();

        PedidoPlatoDto pedidoPlatoDto2 = PedidoPlatoDto.builder()
                .platoId(2)
                .cantidad(2)
                .build();


        List<PedidoPlatoDto> pedidoPlatoDtos =  new ArrayList<>();

        pedidoPlatoDtos.add(pedidoPlatoDto);
        pedidoPlatoDtos.add(pedidoPlatoDto2);

        PedidoDto pedidoDto = PedidoDto.builder()
                .chefId(1)
                .restauranteId(1)
                .clienteId(2)
                .platos(pedidoPlatoDtos)
                .build();

        CategoriaData categoria = CategoriaData.builder()
                .Id(1)
                .Nombre("COMIDA RAPIDA")
                .Descripcion("COMIDA RAPIDA")
                .build();

        RestauranteData restaurante = RestauranteData.builder()
                .id(2)
                .nombre("PAPANATAS")
                .direccion("LOS PATIOS")
                .telefono("+573202040834")
                .urlLogo("urlLogo")
                .nit("123456")
                .idPropietario(1)
                .build();

        PlatoData platoData = PlatoData.builder()
                .precio(100.0)
                .urlImagen("url")
                .nombre("PICADA")
                .descripcion("PLATO")
                .activo(Boolean.TRUE)
                .categoria(categoria)
                .restaurante(restaurante)
                .build();

        Mockito.when(pedidoGateWay.tienePedidoActivo(2)).thenReturn(Mono.just(Boolean.FALSE));
        Mockito.when(pedidoGateWay.maxId()).thenReturn(Mono.just(1));
        Mockito.when(platoRepository.encontrarPlatoPorId(pedidoPlatoDto.getPlatoId())).thenReturn(Mono.just(Optional.of(platoData)));
        Mockito.when(restauranteRepository.existeRestaurante(platoData.getRestaurante().getId().toString())).thenReturn(Mono.just(Optional.empty()));

        var result = useCase.crearPedido(pedidoDto);

        StepVerifier.create(result)
                .expectErrorMessage("last")
                .verify();
    }

}