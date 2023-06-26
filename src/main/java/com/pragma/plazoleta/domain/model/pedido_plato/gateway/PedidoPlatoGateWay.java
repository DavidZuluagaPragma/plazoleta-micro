package com.pragma.plazoleta.domain.model.pedido_plato.gateway;

import com.pragma.plazoleta.domain.model.pedido_plato.PedidoPlato;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PedidoPlatoGateWay {
    Mono<PedidoPlato> crearPedidoPlato(PedidoPlato pedidoPlato);
}
