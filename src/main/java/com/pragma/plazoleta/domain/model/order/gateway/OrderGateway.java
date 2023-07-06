package com.pragma.plazoleta.domain.model.order.gateway;

import com.pragma.plazoleta.domain.model.order.Order;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderGateway {
    Mono<Boolean> hasActiveOrder(Integer clientId);
    Mono<Order> createOrder(Order order);
    Mono<Integer> getMaxId();
    Flux<Order> findOrderByStatus(String status);
    Mono<Order> findOrderById(Integer orderId);
}
