package com.pragma.plazoleta.infrastructure.persistence.pedido;

import com.pragma.plazoleta.aplication.mapper.DataMapper;
import com.pragma.plazoleta.domain.model.order.Order;
import com.pragma.plazoleta.domain.model.order.gateway.OrderGateway;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class OrderDataAdapterRepository implements OrderGateway {

    @Autowired
    OrderDataRepository repository;

    @Override
    public Mono<Boolean> hasActiveOrder(Integer clientId) {
        return Mono.fromCallable(() ->
                        repository.findByClientIdAndStatusIsLike(clientId, "PENDING").isPresent() ?
                                Boolean.TRUE : Boolean.FALSE)
                .onErrorResume(Mono::error);
    }
    @Override
    public Mono<Order> createOrder(Order order) {
        return Mono.fromCallable(() -> repository.save(DataMapper.convertOrderToOrderData(order)))
                .map(DataMapper::convertOrderDataToOrder)
                .onErrorResume(Mono::error);
    }

    @Override
    public Mono<Integer> getMaxId() {
        try {
            int maxId = repository.getMaxId().orElse(0);
            if (maxId == 0) {
                return Mono.just(1);
            }
            return Mono.just(maxId + 1);
        }catch (Exception e){
            return Mono.just(1);
        }
    }

    @Override
    public Flux<Order> findOrderByStatus(String status) {
        return Flux.fromIterable(() -> repository.findAllByStatus(status).iterator())
                .map(DataMapper::convertOrderDataToOrder);
    }
    @Override
    public Mono<Order> findOrderById(Integer orderId) {
        return Mono.fromCallable(() -> repository.findById(orderId).get())
                .map(DataMapper::convertOrderDataToOrder)
                .onErrorResume(throwable -> Mono.error(new BusinessException(BusinessException.Type.ERROR_DATABASE_ORDER_NOT_FOUND)));
    }
}
