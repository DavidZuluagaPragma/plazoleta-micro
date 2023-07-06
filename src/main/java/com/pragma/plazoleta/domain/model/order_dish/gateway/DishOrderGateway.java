package com.pragma.plazoleta.domain.model.order_dish.gateway;

import com.pragma.plazoleta.domain.model.order_dish.DishOrder;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DishOrderGateway {
    Mono<DishOrder> createDishOrder(DishOrder dishOrder);
    Flux<DishOrder> findAllOrdersById(Integer orderId);
}
