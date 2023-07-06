package com.pragma.plazoleta.infrastructure.persistence.pedido_plato;

import com.pragma.plazoleta.aplication.mapper.DataMapper;
import com.pragma.plazoleta.domain.model.order_dish.DishOrder;
import com.pragma.plazoleta.domain.model.order_dish.gateway.DishOrderGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class DishOrderAdapterRepository implements DishOrderGateway {

    @Autowired
    DishOrderDataRepository repository;

    @Override
    public Mono<DishOrder> createDishOrder(DishOrder dishOrder) {
        return Mono.fromCallable(() -> repository.save(DataMapper.convertDishOrderToDishOrderData(dishOrder)))
                .map(DataMapper::convertDishOrderDataToDishOrder)
                .onErrorResume(Mono::error);
    }
    @Override
    public Flux<DishOrder> findAllOrdersById(Integer orderId) {
        return Flux.fromIterable(() -> repository.findAllByOrderId(orderId).iterator())
                .map(DataMapper::convertDishOrderDataToDishOrder)
                .onErrorResume(Mono::error);
    }
}
