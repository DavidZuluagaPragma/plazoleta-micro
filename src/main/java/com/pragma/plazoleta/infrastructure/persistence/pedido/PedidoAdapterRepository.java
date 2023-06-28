package com.pragma.plazoleta.infrastructure.persistence.pedido;

import com.pragma.plazoleta.aplication.mapper.DataMapper;
import com.pragma.plazoleta.domain.model.pedido.Pedido;
import com.pragma.plazoleta.domain.model.pedido.gateway.PedidoGateWay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class PedidoAdapterRepository implements PedidoGateWay {

    @Autowired
    PedidoDataRepository repository;

    @Override
    public Mono<Boolean> tienePedidoActivo(Integer clienteId) {
        return Mono.fromCallable(() ->
                repository.findByClienteIdAndEstadoIsLike(clienteId, "PENDIENTE").isPresent() ?
                        Boolean.TRUE : Boolean.FALSE)
                .onErrorResume(Mono::error);
    }

    @Override
    public Mono<Pedido> crearPedido(Pedido pedido) {
        return Mono.fromCallable(() -> repository.save(DataMapper.convertirPedidoAPedidoData(pedido)))
                .map(DataMapper::convertirPedidoDataAPedido)
                .onErrorResume(Mono::error);
    }

    @Override
    public Mono<Integer> maxId() {
        Optional<Long> idMax = repository.getMaxId();
        if(!idMax.isPresent()){
            return Mono.just(1);
        }
        return Mono.just(idMax.get().intValue() + 1);
    }

    @Override
    public Flux<Pedido> encontrarPedidoPorEstado(String estado) {
        return Flux.fromIterable(() -> repository.findAllByEstado(estado).iterator())
                .map(DataMapper::convertirPedidoDataAPedido);
    }
}
