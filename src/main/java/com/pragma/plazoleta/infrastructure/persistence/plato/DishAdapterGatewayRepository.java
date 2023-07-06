package com.pragma.plazoleta.infrastructure.persistence.plato;

import com.pragma.plazoleta.aplication.mapper.DataMapper;
import com.pragma.plazoleta.domain.model.dish.Dish;
import com.pragma.plazoleta.domain.model.dish.gateway.DishGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Component
public class DishAdapterGatewayRepository implements DishGateway {

    @Autowired
    DishDataRepository repository;
    @Override
    public Mono<Dish> createDish(DishData dishData) {
        return Mono.fromCallable(() -> repository.save(dishData))
                .map(DataMapper::convertDishDataToDish);
    }

    @Override
    public Mono<Optional<DishData>> findDishById(Integer dishId) {
        return Mono.fromCallable(() -> repository.findById(dishId));
    }

    @Override
    public Flux<DishData> findDishByRestaurant(Integer restaurantId) {
        return Flux.fromIterable(() -> repository.findAllByRestaurantId(restaurantId).iterator())
                .onErrorResume(Mono::error);
    }

    @Override
    public List<Dish> listAllRestaurants(Integer restaurantId) {
        return repository.findAllByRestaurantId(restaurantId).stream()
                .map(DataMapper::convertDishDataToDish)
                .toList();
    }
}
