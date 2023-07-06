package com.pragma.plazoleta.domain.model.restaurant.gateway;

import com.pragma.plazoleta.domain.model.restaurant.Restaurant;
import com.pragma.plazoleta.infrastructure.persistence.restaurante.RestaurantData;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface RestaurantGateway {
    Mono<Boolean> isOwner(String userId, String token);
    Mono<Restaurant> createRestaurant(RestaurantData restaurantData);
    Mono<Optional<RestaurantData>> findRestaurant(String restaurantId);
    Mono<Page<RestaurantData>> getRestaurants(int pageNumber, int pageSize);

}
