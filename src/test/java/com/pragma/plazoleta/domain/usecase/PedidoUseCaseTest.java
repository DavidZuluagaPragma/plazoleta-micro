package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.dto.*;
import com.pragma.plazoleta.domain.model.page.Page;
import com.pragma.plazoleta.domain.model.pedido.Pedido;
import com.pragma.plazoleta.domain.model.pedido.gateway.PedidoGateWay;
import com.pragma.plazoleta.domain.model.pedido_plato.PedidoPlato;
import com.pragma.plazoleta.domain.model.pedido_plato.gateway.PedidoPlatoGateWay;
import com.pragma.plazoleta.domain.model.plato.gateway.PlatoRepository;
import com.pragma.plazoleta.domain.model.restaurante.gateway.RestauranteRepository;
import com.pragma.plazoleta.domain.model.twilio.TwilioEstado;
import com.pragma.plazoleta.domain.model.twilio.gateway.TwilioGateWay;
import com.pragma.plazoleta.domain.model.usuario.Usuario;
import com.pragma.plazoleta.domain.model.usuario.gateway.UsuarioGateWay;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @Mock
    UsuarioGateWay usuarioGateWay;

    @Mock
    TwilioGateWay twilioGateWay;

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

        PedidoPlatoRequestDto pedidoPlatoRequestDto = PedidoPlatoRequestDto.builder()
                .platoId(1)
                .cantidad(1)
                .build();

        PedidoPlato pedidoPlato = PedidoPlato.builder()
                .pedidoId(1)
                .platoId(pedidoPlatoRequestDto.getPlatoId())
                .cantidad(pedidoPlatoRequestDto.getCantidad())
                .build();


        List<PedidoPlatoRequestDto> pedidoPlatoRequestDtos = new ArrayList<>();

        pedidoPlatoRequestDtos.add(pedidoPlatoRequestDto);

        PedidoDto pedidoDto = PedidoDto.builder()
                .restauranteId(1)
                .clienteId(2)
                .platos(pedidoPlatoRequestDtos)
                .build();

        Pedido pedido = Pedido.builder()
                .estado("PENDIENTE")
                .chefId(1)
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
        Mockito.when(platoRepository.encontrarPlatoPorId(pedidoPlatoRequestDto.getPlatoId())).thenReturn(Mono.just(Optional.of(platoData)));
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

        PedidoPlatoRequestDto pedidoPlatoRequestDto = PedidoPlatoRequestDto.builder()
                .platoId(1)
                .cantidad(1)
                .build();

        List<PedidoPlatoRequestDto> pedidoPlatoRequestDtos = new ArrayList<>();

        pedidoPlatoRequestDtos.add(pedidoPlatoRequestDto);

        PedidoDto pedidoDto = PedidoDto.builder()
                .restauranteId(1)
                .clienteId(2)
                .platos(pedidoPlatoRequestDtos)
                .build();

        var result = useCase.crearPedido(pedidoDto);

        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.PEDIDO_ACTIVO.getMessage())
                .verify();
    }

    @Test
    void crearPedidoErrorNoExisteRestaurante() {

        PedidoPlatoRequestDto pedidoPlatoRequestDto = PedidoPlatoRequestDto.builder()
                .platoId(1)
                .cantidad(1)
                .build();

        PedidoPlatoRequestDto pedidoPlatoRequestDto2 = PedidoPlatoRequestDto.builder()
                .platoId(2)
                .cantidad(2)
                .build();


        List<PedidoPlatoRequestDto> pedidoPlatoRequestDtos = new ArrayList<>();

        pedidoPlatoRequestDtos.add(pedidoPlatoRequestDto);
        pedidoPlatoRequestDtos.add(pedidoPlatoRequestDto2);

        PedidoDto pedidoDto = PedidoDto.builder()
                .restauranteId(1)
                .clienteId(2)
                .platos(pedidoPlatoRequestDtos)
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
        Mockito.when(platoRepository.encontrarPlatoPorId(pedidoPlatoRequestDto.getPlatoId())).thenReturn(Mono.just(Optional.of(platoData)));
        Mockito.when(restauranteRepository.existeRestaurante(platoData.getRestaurante().getId().toString())).thenReturn(Mono.just(Optional.empty()));

        var result = useCase.crearPedido(pedidoDto);

        StepVerifier.create(result)
                .expectErrorMessage("last")
                .verify();
    }

    @Test
    void listarPedidosPorEstadoExitoso() {

        Pedido pedido = Pedido.builder()
                .id(1)
                .estado("PENDIENTE")
                .chefId(17)
                .clienteId(1)
                .fecha(new Date())
                .restauranteId(1)
                .build();

        UsuarioPedidoRequestDto usuarioPedidoRequestDto = UsuarioPedidoRequestDto.builder()
                .chef(pedido.getChefId())
                .cliente(pedido.getClienteId())
                .build();


        UsuarioPedidoDto usuarioPedidoDto = UsuarioPedidoDto.builder()
                .chef(Usuario.builder()
                        .id(17)
                        .build())
                .cliente(Usuario.builder()
                        .id(1)
                        .build())
                .build();

        PedidoPlato pedidoPlato = PedidoPlato.builder()
                .platoId(1)
                .cantidad(1)
                .build();

        PlatoData platoData = PlatoData.builder()
                .id(1)
                .precio(100.0)
                .urlImagen("url")
                .nombre("PICADA")
                .descripcion("PLATO")
                .activo(Boolean.TRUE)
                .categoria(CategoriaData.builder().build())
                .restaurante(RestauranteData.builder().build())
                .build();

        PlatoResponseDto platoResponseDto = PlatoResponseDto.builder()
                .id(platoData.getId())
                .precio(platoData.getPrecio())
                .descripcion(platoData.getDescripcion())
                .nombre(platoData.getNombre())
                .cantidad(pedidoPlato.getCantidad())
                .build();

        PedidoListaDto pedidoListaDto = PedidoListaDto.builder()
                .id(1)
                .chef(usuarioPedidoDto.getChef())
                .cliente(usuarioPedidoDto.getCliente())
                .estado(pedido.getEstado())
                .listaDePlatos(List.of(platoResponseDto))
                .build();

        Page<PedidoListaDto> pageEsperado = new Page<>(List.of(pedidoListaDto),List.of(pedidoListaDto).stream().count());

        Mockito.when(pedidoGateWay.encontrarPedidoPorEstado("PENDIENTE")).thenReturn(Flux.just(pedido));
        Mockito.when(usuarioGateWay.conseguirUsuariosDelPedido(usuarioPedidoRequestDto, "TOKEN")).thenReturn(Mono.just(usuarioPedidoDto));
        Mockito.when(pedidoPlatoGateWay.encontrarTodosLosPedidosPorId(pedido.getId())).thenReturn(Flux.just(pedidoPlato));
        Mockito.when(platoRepository.encontrarPlatoPorId(pedidoPlato.getPlatoId())).thenReturn(Mono.just(Optional.of(platoData)));

        var result = useCase.listarPedidosPorEstado("PENDIENTE", "TOKEN");

        StepVerifier.create(result)
                .expectNext(pageEsperado)
                .expectComplete()
                .verify();
    }

    @Test
    void asignarPedidoExitoso() {
        Pedido pedido = Pedido.builder()
                .id(1)
                .estado("PENDIENTE")
                .chefId(17)
                .clienteId(1)
                .fecha(new Date())
                .restauranteId(1)
                .build();

        AssignOrderRequestDto assignOrderRequestDto = AssignOrderRequestDto.builder()
                .estado("EN PREPARACIÓN")
                .pedidoId(pedido.getId())
                .build();

        UsuarioPedidoRequestDto usuarioPedidoRequestDto = UsuarioPedidoRequestDto.builder()
                .chef(pedido.getChefId())
                .cliente(pedido.getClienteId())
                .build();


        UsuarioPedidoDto usuarioPedidoDto = UsuarioPedidoDto.builder()
                .chef(Usuario.builder()
                        .id(17)
                        .build())
                .cliente(Usuario.builder()
                        .id(1)
                        .build())
                .build();

        PedidoPlato pedidoPlato = PedidoPlato.builder()
                .platoId(1)
                .cantidad(1)
                .build();

        PlatoData platoData = PlatoData.builder()
                .id(1)
                .precio(100.0)
                .urlImagen("url")
                .nombre("PICADA")
                .descripcion("PLATO")
                .activo(Boolean.TRUE)
                .categoria(CategoriaData.builder().build())
                .restaurante(RestauranteData.builder().build())
                .build();

        PlatoResponseDto platoResponseDto = PlatoResponseDto.builder()
                .id(platoData.getId())
                .precio(platoData.getPrecio())
                .descripcion(platoData.getDescripcion())
                .nombre(platoData.getNombre())
                .cantidad(pedidoPlato.getCantidad())
                .build();

        PedidoListaDto pedidoListaDto = PedidoListaDto.builder()
                .id(1)
                .chef(usuarioPedidoDto.getChef())
                .cliente(usuarioPedidoDto.getCliente())
                .estado(assignOrderRequestDto.getEstado())
                .listaDePlatos(List.of(platoResponseDto))
                .build();

        Mockito.when(pedidoGateWay.encontrarPedidoPorId(pedido.getId())).thenReturn(Mono.just(pedido));
        Mockito.when(pedidoGateWay.crearPedido(pedido
                .toBuilder()
                        .chefId(17)
                        .estado(assignOrderRequestDto.getEstado())
                .build())).thenReturn(Mono.just(pedido));
        Mockito.when(usuarioGateWay.conseguirUsuariosDelPedido(usuarioPedidoRequestDto, "TOKEN")).thenReturn(Mono.just(usuarioPedidoDto));
        Mockito.when(pedidoPlatoGateWay.encontrarTodosLosPedidosPorId(pedido.getId())).thenReturn(Flux.just(pedidoPlato,pedidoPlato));
        Mockito.when(platoRepository.encontrarPlatoPorId(pedidoPlato.getPlatoId())).thenReturn(Mono.just(Optional.of(platoData)));

        var result = useCase.asignarPedido("17",assignOrderRequestDto, "TOKEN");

        StepVerifier.create(result)
                .expectNext(pedidoListaDto)
                .expectComplete()
                .verify();
    }

    @Test
    void pedidoListoExitoso() {
        Pedido pedido = Pedido.builder()
                .id(1)
                .estado("PENDIENTE")
                .chefId(17)
                .clienteId(1)
                .fecha(new Date())
                .restauranteId(1)
                .build();

        UsuarioPedidoRequestDto usuarioPedidoRequestDto = UsuarioPedidoRequestDto.builder()
                .chef(0)
                .cliente(pedido.getClienteId())
                .build();

        UsuarioPedidoDto usuarioPedidoDto = UsuarioPedidoDto.builder()
                .chef(Usuario.builder()
                        .build())
                .cliente(Usuario.builder()
                        .id(1)
                        .celular("+573202040834")
                        .build())
                .build();

        EnviarMensajeDto enviarMensajeDto = EnviarMensajeDto.builder()
                .numeroDestino(usuarioPedidoDto.getCliente().getCelular())
                .usuario("javatechie")
                .build();

        RespuestaMensajeDto respuestaMensajeDto = RespuestaMensajeDto.builder()
                .mensaje("HOLA")
                .estado(TwilioEstado.DELIVERED)
                .build();

        Mockito.when(pedidoGateWay.encontrarPedidoPorId(pedido.getId())).thenReturn(Mono.just(pedido));
        Mockito.when(pedidoGateWay.crearPedido(pedido.toBuilder()
                        .estado("PEDIDO LISTO")
                .build())).thenReturn(Mono.just(pedido));
        Mockito.when(usuarioGateWay.conseguirUsuariosDelPedido(usuarioPedidoRequestDto,"TOKEN")).thenReturn(Mono.just(usuarioPedidoDto));
        Mockito.when(twilioGateWay.enviarMensaje(enviarMensajeDto, "TOKEN")).thenReturn(Mono.just(respuestaMensajeDto));

        var result = useCase.pedidoListo("1", "TOKEN");

        StepVerifier.create(result)
                .expectNext(respuestaMensajeDto)
                .expectComplete();

    }


    @Test
    void pedidoCompletoExitoso() {

        CompletarPedidoDto completarPedidoDto = CompletarPedidoDto.builder()
                .codigo("123456")
                .pedidoId(1)
                .build();

        EnviarMensajeDto enviarMensajeDto = EnviarMensajeDto.builder()
                .tiempoContra("123456")
                .usuario("usuario")
                .build();

        Pedido pedido = Pedido.builder()
                .id(1)
                .estado("LISTO")
                .chefId(17)
                .clienteId(1)
                .fecha(new Date())
                .restauranteId(1)
                .build();

        String pedidoCompleto = "PEDIDO COMPLETADO!!";

        Mockito.when(twilioGateWay.validarCodigo(enviarMensajeDto, "TOKEN")).thenReturn(Mono.just("MENSAJE ENVIADO"));
        Mockito.when(pedidoGateWay.encontrarPedidoPorId(pedido.getId())).thenReturn(Mono.just(pedido));
        Mockito.when(pedidoGateWay.crearPedido(pedido.toBuilder()
                        .estado("ENTREGADO")
                .build())).thenReturn(Mono.just(pedido));

        var result = useCase.completarPedido(completarPedidoDto, "TOKEN");

        StepVerifier.create(result)
                .expectNext(pedidoCompleto)
                .expectComplete()
                .verify();
    }

    @Test
    void pedidoCompletoErrorMensajeNoAceptado() {

        CompletarPedidoDto completarPedidoDto = CompletarPedidoDto.builder()
                .codigo("123456")
                .pedidoId(1)
                .build();

        EnviarMensajeDto enviarMensajeDto = EnviarMensajeDto.builder()
                .tiempoContra("123456")
                .usuario("usuario")
                .build();


        Mockito.when(twilioGateWay.validarCodigo(enviarMensajeDto, "TOKEN")).thenReturn(Mono.just("Otp invalido prueba de nuevo !"));

        var result = useCase.completarPedido(completarPedidoDto, "TOKEN");

        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.ERROR_PEDIDO_COMPLETADO.getMessage())
                .verify();
    }

    @Test
    void pedidoCompletoErrorPedidoNoEstabaListo() {

        CompletarPedidoDto completarPedidoDto = CompletarPedidoDto.builder()
                .codigo("123456")
                .pedidoId(1)
                .build();

        EnviarMensajeDto enviarMensajeDto = EnviarMensajeDto.builder()
                .tiempoContra("123456")
                .usuario("usuario")
                .build();

        Pedido pedido = Pedido.builder()
                .id(1)
                .estado("PENDIENTE")
                .chefId(17)
                .clienteId(1)
                .fecha(new Date())
                .restauranteId(1)
                .build();

        Mockito.when(twilioGateWay.validarCodigo(enviarMensajeDto, "TOKEN")).thenReturn(Mono.just("Otp invalido prueba de nuevo !"));
        Mockito.when(pedidoGateWay.encontrarPedidoPorId(pedido.getId())).thenReturn(Mono.just(pedido));

        var result = useCase.completarPedido(completarPedidoDto, "TOKEN");

        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.ERROR_PEDIDO_COMPLETADO.getMessage())
                .verify();
    }

}