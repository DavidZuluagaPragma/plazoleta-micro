package com.pragma.plazoleta.domain.model.pedido.gateway;

import com.pragma.plazoleta.domain.model.pedido.Pedido;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PedidoGateWay {
    Mono<Boolean> tienePedidoActivo(Integer clienteId);
    Mono<Pedido> crearPedido(Pedido pedido);
    Mono<Integer> maxId();
    Flux<Pedido> encontrarPedidoPorEstado(String estado);

    Mono<Pedido> encontrarPedidoPorId(Integer pedidoId);
}
