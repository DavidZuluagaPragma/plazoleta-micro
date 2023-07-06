package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.dto.RestaurantDto;
import com.pragma.plazoleta.aplication.dto.RestaurantResponseDto;
import com.pragma.plazoleta.aplication.mapper.DataMapper;
import com.pragma.plazoleta.aplication.mapper.DtoMapper;
import com.pragma.plazoleta.domain.model.common.ValidationRestaurant;
import com.pragma.plazoleta.domain.model.page.Page;
import com.pragma.plazoleta.domain.model.restaurant.Restaurant;
import com.pragma.plazoleta.domain.model.restaurant.gateway.RestaurantGateway;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RestaurantUseCase {

    @Autowired
    RestaurantGateway restaurantGateway;

    /**
     * Método para crear un restaurante.
     *
     * @param restaurant objeto con los datos necesarios para crear el restaurante
     * @param token      el token de autenticación
     * @return un objeto de tipo Restaurant
     */
    public Mono<RestaurantDto> createRestaurant(RestaurantDto restaurant, String token) {
        return Mono.fromCallable(() -> DtoMapper.convertRestaurantDtoToRestaurant(restaurant))
                .flatMap(ValidationRestaurant::validateRestaurantDto)
                .flatMap(validatedRestaurant -> restaurantGateway.isOwner(restaurant.getOwnerId().toString(), token)
                        .flatMap(isOwner -> {
                            if (!isOwner) {
                                return Mono.error(new BusinessException(BusinessException.Type.USER_NOT_OWNER));
                            }
                            return restaurantGateway.createRestaurant(DataMapper.convertRestaurantToRestaurantData(validatedRestaurant));
                        }))
                .map(DtoMapper::convertRestaurantToRestaurantDto);
    }

    /**
     * Método para verificar si el usuario es un propetario.
     *
     * @param userId id del usuario
     * @param token  el token de autenticación
     * @return un verdadero o falso
     */
    public Mono<Boolean> isOwner(String userId, String token) {
        return restaurantGateway.isOwner(userId, token);
    }

    /**
     * Método para verificar si existe el restaurante.
     *
     * @param restaurantId id del restaurante
     * @return objeto de tipo Restaurant
     */
    public Mono<Restaurant> checkRestaurantExists(String restaurantId) {
        return restaurantGateway.findRestaurant(restaurantId)
                .flatMap(restaurantData -> {
                    if (!restaurantData.isPresent()) {
                        return Mono.error(new BusinessException(BusinessException.Type.ERROR_DATABASE_RESTAURANT_NOT_FOUND));
                    }
                    return Mono.just(DataMapper.convertRestaurantDataToRestaurant(restaurantData.get()));
                });
    }

    /**
     * Método para conseguir todos los restaurantes.
     *
     * @param pageNumber numero de pagina
     * @param pageSize   tamaño de pagina
     * @return objeto de tipo RestaurantResponseDto con paginado
     */
    public Mono<Page<RestaurantResponseDto>> getRestaurants(int pageNumber, int pageSize) {
        return restaurantGateway.getRestaurants(pageNumber, pageSize)
                .map(restaurantData -> restaurantData.stream()
                        .map(DtoMapper::convertRestaurantDataToRestaurantResponse)
                        .collect(Collectors.toList()))
                .map(restaurantData -> new Page<>(restaurantData, restaurantData.stream().count()))
                .onErrorResume(Mono::error);
    }
}
