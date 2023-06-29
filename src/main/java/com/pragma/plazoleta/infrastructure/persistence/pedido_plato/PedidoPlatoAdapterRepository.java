package com.pragma.plazoleta.infrastructure.persistence.pedido_plato;

import com.pragma.plazoleta.aplication.mapper.DataMapper;
import com.pragma.plazoleta.domain.model.pedido_plato.PedidoPlato;
import com.pragma.plazoleta.domain.model.pedido_plato.gateway.PedidoPlatoGateWay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class PedidoPlatoAdapterRepository implements PedidoPlatoGateWay {

    @Autowired
    PedidoPlatoDataRepository repository;

    @Override
    public Mono<PedidoPlato> crearPedidoPlato(PedidoPlato pedidoPlato) {
        return Mono.fromCallable(() -> repository.save(DataMapper.convertirPedidoPlatoAPedidoPlatoData(pedidoPlato)))
                .map(DataMapper::convertirPedidoPlatoDataAPedidoPlato)
                .onErrorResume(Mono::error);
    }

    @Override
    public Flux<PedidoPlato> encontrarTodosLosPedidosPorId(Integer pedidoId) {
        return Flux.fromIterable(() -> repository.findAllByPedidoId(pedidoId).iterator())
                .map(DataMapper::convertirPedidoPlatoDataAPedidoPlato)
                .onErrorResume(Mono::error);
    }
}
