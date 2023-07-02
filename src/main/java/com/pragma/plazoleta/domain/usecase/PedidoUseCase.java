package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.dto.*;
import com.pragma.plazoleta.aplication.utils.Utils;
import com.pragma.plazoleta.domain.model.page.Page;
import com.pragma.plazoleta.domain.model.pedido.Pedido;
import com.pragma.plazoleta.domain.model.pedido.gateway.PedidoGateWay;
import com.pragma.plazoleta.domain.model.pedido_plato.PedidoPlato;
import com.pragma.plazoleta.domain.model.pedido_plato.gateway.PedidoPlatoGateWay;
import com.pragma.plazoleta.domain.model.plato.gateway.PlatoRepository;
import com.pragma.plazoleta.domain.model.restaurante.gateway.RestauranteRepository;
import com.pragma.plazoleta.domain.model.traceability.gateway.TraceabilityGateway;
import com.pragma.plazoleta.domain.model.twilio.gateway.TwilioGateWay;
import com.pragma.plazoleta.domain.model.usuario.Usuario;
import com.pragma.plazoleta.domain.model.usuario.gateway.UsuarioGateWay;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@RequiredArgsConstructor
@Component
public class PedidoUseCase {

    @Autowired
    PedidoGateWay pedidoGateWay;

    @Autowired
    PedidoPlatoGateWay pedidoPlatoGateWay;

    @Autowired
    PlatoRepository platoRepository;

    @Autowired
    RestauranteRepository restauranteRepository;

    @Autowired
    UsuarioGateWay usuarioGateWay;

    @Autowired
    TwilioGateWay twilioGateWay;

    @Autowired
    TraceabilityGateway traceabilityGateway;

    private static final String PEDIDO_PENTIENDE = "PENDIENTE";
    private static final String PEDIDO_LISTO = "LISTO";
    private static final String PEDIDO_PREPARACION = "EN PREPARACIÓN";
    private static final String PEDIDO_ENTREGADO = "ENTREGADO";
    private static final String OTP_INVALIDO = "Otp invalido prueba de nuevo !";
    private static final String PEDIDO_COMPLETADO = "PEDIDO COMPLETADO!";
    private static final String PEDIDO_CANCELADO = "CANCELADO";
    private static final String PEDIDO_CANCELADO_EXITO = "PEDIDO CANCELADO CON EXITO";
    private static final String USUARIO = "javatechie";

    public Flux<PedidoResponse> crearPedido(PedidoDto pedidoDto, String token) {
        if (pedidoDto.getPlatos().isEmpty()) {
            return Flux.error(new BusinessException(BusinessException.Type.NO_TIENE_PLATOS));
        }
        return pedidoGateWay.tienePedidoActivo(pedidoDto.getClienteId())
                .flatMapMany(tieneActivoPedido -> {
                    if (Boolean.TRUE.equals(tieneActivoPedido)) {
                        return Flux.error(new BusinessException(BusinessException.Type.PEDIDO_ACTIVO));
                    }

                    return pedidoGateWay.maxId()
                            .flatMapMany(pedidoId -> Flux.fromIterable(pedidoDto.getPlatos())
                                    .flatMap(platos -> platoRepository.encontrarPlatoPorId(platos.getPlatoId())
                                            .flatMap(platoData -> platoData.map(data -> restauranteRepository.existeRestaurante(data.getRestaurante().getId().toString())
                                                    .flatMap(restauranteData -> {
                                                        if (restauranteData.isEmpty()) {
                                                            return Mono.error(new BusinessException(BusinessException.Type.ERROR_BASE_DATOS_RESTAURANTE_NO_ENCONTRADA));
                                                        }
                                                        if (!Objects.equals(restauranteData.get().getId(), pedidoDto.getRestauranteId())) {
                                                            return Mono.error(new BusinessException(BusinessException.Type.PLATO_DE_OTRO_RESTAURANTE));
                                                        }
                                                        return pedidoPlatoGateWay.crearPedidoPlato(PedidoPlato.builder()
                                                                .pedidoId(pedidoId)
                                                                .platoId(platos.getPlatoId())
                                                                .cantidad(platos.getCantidad())
                                                                .build());
                                                    })).orElseGet(() -> Mono.error(new BusinessException(BusinessException.Type.ERROR_BASE_DATOS_PLATO_NO_ENCONTRADO)))
                                            ))
                                    .then(pedidoGateWay.crearPedido(Pedido.builder()
                                            .estado(PEDIDO_PENTIENDE)
                                            .chefId(0)
                                            .clienteId(pedidoDto.getClienteId())
                                            .fecha(new Date())
                                            .restauranteId(pedidoDto.getRestauranteId())
                                            .build()))
                                    .flatMap(pedido -> traceabilityGateway.createTraceabilityForOrder(TraceabilityRequestDto.builder()
                                                    .orderId(pedido.getId())
                                                    .clientId(pedido.getId())
                                                    .newStatus(PEDIDO_PENTIENDE)
                                                    .build(), token)
                                            .thenReturn(PedidoResponse.builder()
                                                    .estado(PEDIDO_PENTIENDE)
                                                    .fecha(pedido.getFecha())
                                                    .build())));
                });
    }


    public Mono<Page<PedidoListaDto>> listarPedidosPorEstado(String estado, String token) {
        return pedidoGateWay.encontrarPedidoPorEstado(estado)
                .flatMap(pedido -> {
                    List<PlatoResponseDto> listaPlatos = new ArrayList<>();
                    return usuarioGateWay.conseguirUsuariosDelPedido(UsuarioPedidoRequestDto.builder()
                                    .chef(pedido.getChefId())
                                    .cliente(pedido.getClienteId())
                                    .build(), token)
                            .flatMap(usuarioPedidoDto -> pedidoPlatoGateWay.encontrarTodosLosPedidosPorId(pedido.getId())
                                    .flatMap(pedidoPlato -> platoRepository.encontrarPlatoPorId(pedidoPlato.getPlatoId())
                                            .map(platoData -> PlatoResponseDto.builder()
                                                    .id(platoData.get().getId())
                                                    .descripcion(platoData.get().getDescripcion())
                                                    .precio(platoData.get().getPrecio())
                                                    .nombre(platoData.get().getNombre())
                                                    .cantidad(pedidoPlato.getCantidad())
                                                    .build())
                                            .doOnNext(listaPlatos::add))
                                    .then(Mono.just(PedidoListaDto.builder()
                                            .id(pedido.getId())
                                            .cliente(usuarioPedidoDto.getCliente())
                                            .chef(usuarioPedidoDto.getChef())
                                            .estado(estado)
                                            .listaDePlatos(listaPlatos)
                                            .build())));
                })
                .collectList()
                .map(pedidoListaDtos -> new Page<>(pedidoListaDtos, pedidoListaDtos.stream().count()));
    }

    public Mono<PedidoListaDto> asignarPedido(String usuarioId, AssignOrderRequestDto assignOrderRequestDto, String token) {
        return pedidoGateWay.encontrarPedidoPorId(assignOrderRequestDto.getPedidoId())
                .flatMap(pedido -> pedidoGateWay.crearPedido(pedido.toBuilder()
                                .chefId(Integer.parseInt(usuarioId))
                                .estado(PEDIDO_PREPARACION)
                                .build())
                        .flatMap(pedidoModificado -> usuarioGateWay.conseguirUsuariosDelPedido(UsuarioPedidoRequestDto.builder()
                                        .chef(Integer.parseInt(usuarioId))
                                        .cliente(pedido.getClienteId())
                                        .build(), token)
                                .flatMap(usuarioPedidoDto -> pedidoPlatoGateWay.encontrarTodosLosPedidosPorId(pedido.getId())
                                        .flatMap(pedidoPlato -> platoRepository.encontrarPlatoPorId(pedidoPlato.getPlatoId())
                                                .map(platoData -> PlatoResponseDto.builder()
                                                        .id(platoData.get().getId())
                                                        .descripcion(platoData.get().getDescripcion())
                                                        .precio(platoData.get().getPrecio())
                                                        .nombre(platoData.get().getNombre())
                                                        .cantidad(pedidoPlato.getCantidad())
                                                        .build()))
                                        .collectList() // Esto recogerá todos los elementos en una lista
                                        .flatMap(listaPlatos -> {
                                            listaPlatos.remove(0);
                                            return traceabilityGateway.createTraceabilityForOrder(TraceabilityRequestDto.builder()
                                                            .orderId(pedido.getId())
                                                            .employedId(usuarioPedidoDto.getChef().getId())
                                                            .employedEmail(usuarioPedidoDto.getChef().getCorreo())
                                                            .clientEmail(usuarioPedidoDto.getCliente().getCorreo())
                                                            .newStatus(PEDIDO_PREPARACION)
                                                            .build(), token)
                                                    .thenReturn(PedidoListaDto.builder()
                                                            .id(pedido.getId())
                                                            .cliente(usuarioPedidoDto.getCliente())
                                                            .chef(usuarioPedidoDto.getChef())
                                                            .estado(assignOrderRequestDto.getEstado())
                                                            .listaDePlatos(listaPlatos)
                                                            .build());
                                        }))));
    }

    public Mono<RespuestaMensajeDto> pedidoListo(String pedidoId, String token) {
        return pedidoGateWay.encontrarPedidoPorId(Integer.parseInt(pedidoId))
                .flatMap(pedido -> pedidoGateWay.crearPedido(pedido.toBuilder()
                        .estado(PEDIDO_LISTO)
                        .build()))
                .flatMap(pedido -> usuarioGateWay.conseguirUsuariosDelPedido(UsuarioPedidoRequestDto.builder()
                        .chef(0)
                        .cliente(pedido.getClienteId())
                        .build(), token))
                .flatMap(usuarioPedidoDto -> traceabilityGateway.createTraceabilityForOrder(TraceabilityRequestDto.builder()
                                .orderId(Integer.parseInt(pedidoId))
                                .newStatus(PEDIDO_LISTO)
                                .build(), token)
                        .flatMap(traceability -> twilioGateWay.enviarMensaje(EnviarMensajeDto.builder()
                                .numeroDestino(usuarioPedidoDto.getCliente().getCelular())
                                .usuario(USUARIO)
                                .build(), token)));

    }

    public Mono<String> completarPedido(CompletarPedidoDto completarPedidoDto, String token) {
        return twilioGateWay.validarCodigo(EnviarMensajeDto.builder()
                        .tiempoContra(completarPedidoDto.getCodigo())
                        .usuario(USUARIO)
                        .build(), token)
                .flatMap(mensajeRespuesta -> {
                    if (mensajeRespuesta.equals(OTP_INVALIDO)) {
                        return Mono.error(new BusinessException(BusinessException.Type.ERROR_PEDIDO_COMPLETADO));
                    }
                    return Mono.just(mensajeRespuesta);
                })
                .flatMap(respuesta -> pedidoGateWay.encontrarPedidoPorId(completarPedidoDto.getPedidoId())
                        .flatMap(pedido -> {
                            if (!pedido.getEstado().equals(PEDIDO_LISTO)) {
                                return Mono.error(new BusinessException(BusinessException.Type.ERROR_PEDIDO_COMPLETADO));
                            }
                            return pedidoGateWay.crearPedido(pedido.toBuilder()
                                    .estado(PEDIDO_ENTREGADO)
                                    .build());
                        }))
                .flatMap(pedido -> traceabilityGateway.createTraceabilityForOrder(TraceabilityRequestDto.builder()
                        .orderId(pedido.getId())
                        .newStatus(PEDIDO_ENTREGADO)
                        .build(), token))
                .thenReturn(PEDIDO_COMPLETADO)
                .onErrorResume(Mono::error);
    }

    public Mono<String> cancelarPedido(Integer pedidoId, String token) {
        return pedidoGateWay.encontrarPedidoPorId(pedidoId)
                .flatMap(pedido -> {
                    if (!pedido.getEstado().equals(PEDIDO_PENTIENDE)) {
                        return Mono.error(new BusinessException(BusinessException.Type.ERROR_CANCELAR_PEDIDO));
                    }
                    return pedidoGateWay.crearPedido(pedido.toBuilder()
                            .estado(PEDIDO_CANCELADO)
                            .build());
                })
                .flatMap(pedido -> traceabilityGateway.createTraceabilityForOrder(TraceabilityRequestDto.builder()
                        .orderId(pedido.getId())
                        .newStatus(PEDIDO_CANCELADO)
                        .build(), token))
                .thenReturn(PEDIDO_CANCELADO_EXITO);
    }

    public Flux<TraceabilityByOrderResponse> getTraceabilityByOrder(String token) {
        return traceabilityGateway.getAllCompletedTraceability(token)
                .flatMap(traceability -> pedidoGateWay.encontrarPedidoPorId(traceability.getOrderId())
                        .map(pedido -> TraceabilityByOrderResponse.builder()
                                .orderId(pedido.getId())
                                .startDate(pedido.getFecha())
                                .endDate(traceability.getDate())
                                .efficiency(pedido.calculateOrderTime(traceability.getDate()))
                                .build()));
    }

    public Flux<TraceabilityByEmployedResponse> getTraceabilityByEmployed(String token) {
        return traceabilityGateway.getAllCompletedTraceability(token)
                .flatMap(traceability -> pedidoGateWay.encontrarPedidoPorId(traceability.getOrderId())
                        .flatMap(pedido -> usuarioGateWay.findUserById(traceability.getEmployedId(), token)
                                .map(usuario -> {
                                    long timeHalf = traceability.getDate().getTime() - pedido.getFecha().getTime();
                                    return new TraceabilityByEmployedResponse(usuario, Long.toString(timeHalf));
                                })
                        )
                )
                .groupBy(TraceabilityByEmployedResponse::getEmployed) // Group by employee
                .flatMap(groupedFlux -> groupedFlux.collectList()
                        .map(traceabilityByEmployedResponses -> {
                            Usuario employee = traceabilityByEmployedResponses.get(0).getEmployed(); // Get the employee of the group
                            String efficiencyAverage = Utils.calculateAverageEfficiency(traceabilityByEmployedResponses); // Calculate average efficiency
                            return new TraceabilityByEmployedResponse(employee, efficiencyAverage);
                        })
                );
    }

}
