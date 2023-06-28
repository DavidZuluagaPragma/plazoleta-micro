package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.dto.*;
import com.pragma.plazoleta.domain.model.page.Page;
import com.pragma.plazoleta.domain.model.pedido.Pedido;
import com.pragma.plazoleta.domain.model.pedido.gateway.PedidoGateWay;
import com.pragma.plazoleta.domain.model.pedido_plato.PedidoPlato;
import com.pragma.plazoleta.domain.model.pedido_plato.gateway.PedidoPlatoGateWay;
import com.pragma.plazoleta.domain.model.plato.gateway.PlatoRepository;
import com.pragma.plazoleta.domain.model.restaurante.gateway.RestauranteRepository;
import com.pragma.plazoleta.domain.model.usuario.Usuario;
import com.pragma.plazoleta.domain.model.usuario.gateway.UsuarioGateWay;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

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

    private static final String PEDIDO_PENTIENDE = "PENDIENTE";

    public Flux<PedidoResponse> crearPedido(PedidoDto pedidoDto) {
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
                                            .chefId(pedidoDto.getChefId())
                                            .clienteId(pedidoDto.getClienteId())
                                            .fecha(new Date())
                                            .restauranteId(pedidoDto.getRestauranteId())
                                            .build()))
                                    .map(pedido -> PedidoResponse.builder()
                                            .estado(PEDIDO_PENTIENDE)
                                            .fecha(pedido.getFecha())
                                            .build()));
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
                                            .cliente(usuarioPedidoDto.getCliente())
                                            .chef(usuarioPedidoDto.getChef())
                                            .estado(estado)
                                            .listaDePlatos(listaPlatos)
                                            .build())));
                })
                .collectList()
                .map(pedidoListaDtos -> new Page<>(pedidoListaDtos, pedidoListaDtos.stream().count()));
    }
}
