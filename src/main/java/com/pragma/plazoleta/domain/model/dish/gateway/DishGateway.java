package com.pragma.plazoleta.domain.model.dish.gateway;

import com.pragma.plazoleta.domain.model.dish.Dish;
import com.pragma.plazoleta.infrastructure.persistence.plato.DishData;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Repository
public interface DishGateway {
    Mono<Dish> createDish(DishData dishData);
    Mono<Optional<DishData>> findDishById(Integer dishId);
    Flux<DishData> findDishByRestaurant(Integer restaurantId);
    List<Dish> listAllRestaurants(Integer dishId);
}
