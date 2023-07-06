package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.dto.DishChangeStatusDto;
import com.pragma.plazoleta.aplication.dto.DishDto;
import com.pragma.plazoleta.aplication.dto.DishEditDto;
import com.pragma.plazoleta.aplication.dto.PlateResponseDto;
import com.pragma.plazoleta.aplication.mapper.DataMapper;
import com.pragma.plazoleta.domain.model.page.Page;
import com.pragma.plazoleta.domain.model.dish.Dish;
import com.pragma.plazoleta.domain.model.dish.gateway.DishGateway;
import com.pragma.plazoleta.domain.model.restaurant.gateway.RestaurantGateway;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import com.pragma.plazoleta.infrastructure.persistence.plato.DishData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class DishUseCase {

    @Autowired
    DishGateway dishGateway;

    @Autowired
    RestaurantUseCase restaurantUseCase;

    @Autowired
    RestaurantGateway restaurantGateway;

    @Autowired
    CategoryUseCase categoryUseCase;


    /**
     * Método para crear plato.
     *
     * @param dishDto objeto con los datos necesarios para crear el plato
     * @param userId  id del usuario
     * @param token   el token de autenticación
     * @return plato creado
     */
    public Mono<Dish> createDish(DishDto dishDto, String userId, String token) {
        // Comprueba si el usuario es el propietario del restaurante
        return restaurantUseCase.isOwner(userId, token)
                .flatMap(isOwner -> {
                    // Si el usuario no es el propietario, devuelve un error
                    if (!isOwner) {
                        return Mono.error(new BusinessException(BusinessException.Type.USER_NOT_OWNER));
                    }
                    // Comprueba si el restaurante existe
                    return restaurantUseCase.checkRestaurantExists(dishDto.getRestaurantId().toString())
                            // Si el restaurante existe, busca la categoría del plato
                            .flatMap(restaurant -> categoryUseCase.getCategoryById(dishDto.getCategoryId().toString())
                                    // Si la categoría existe, crea el plato
                                    .flatMap(category -> dishGateway.createDish(DishData.builder()
                                            .name(dishDto.getName())
                                            .description(dishDto.getDescription())
                                            .imageUrl(dishDto.getImageUrl())
                                            .price(dishDto.getPrice())
                                            .active(Boolean.TRUE)
                                            .category(DataMapper.convertCategoryToCategoryData(category))
                                            .restaurant(DataMapper.convertRestaurantToRestaurantData(restaurant))
                                            .build())));
                });
    }

    /**
     * Método para crear plato.
     *
     * @param dishDto objeto con los datos necesarios para editar el plato
     * @param userId  id del usuario
     * @param token   el token de autenticación
     * @return plato modificado
     */
    public Mono<Dish> edit(DishEditDto dishDto, String userId, String token) {
        // Comprueba si el usuario es el propietario del restaurante
        return restaurantUseCase.isOwner(userId, token)
                .flatMap(isOwner -> {
                    // Si el usuario no es el propietario, devuelve un error
                    if (Boolean.FALSE.equals(isOwner)) {
                        return Mono.error(new BusinessException(BusinessException.Type.USER_NOT_OWNER));
                    }
                    // Si el plato no tiene un id, devuelve un error
                    if (dishDto.getId() == null) {
                        return Mono.error(new BusinessException(BusinessException.Type.EDIT_DISH_ERROR));
                    }
                    // Busca el plato por su id
                    return dishGateway.findDishById(dishDto.getId())
                            .flatMap(dishData -> {
                                // Si el plato no existe, devuelve un error
                                if (!dishData.isPresent()) {
                                    return Mono.error(new BusinessException(BusinessException.Type.ERROR_DATABASE_DISH_NOT_FOUND));
                                }
                                // Si el plato existe, lo edita
                                return dishGateway.createDish(dishData.get()
                                        .toBuilder()
                                        .price(dishDto.getPrice())
                                        .description(dishDto.getDescription())
                                        .build());
                            });
                });
    }

    /**
     * Método para cambiar el estado del plato.
     *
     * @param dishDto objeto con los datos necesarios para editar el plato
     * @param userId  id del usuario
     * @return plato modificado
     */
    public Mono<Dish> changeDishStatus(DishChangeStatusDto dishDto, String userId) {
        // Comprueba si el restaurante existe
        return restaurantUseCase.checkRestaurantExists(dishDto.getRestaurantId())
                .flatMap(restaurant -> {
                    // Si el usuario no es el propietario del restaurante, devuelve un error
                    if (restaurant.getOwnerId() != Integer.parseInt(userId)) {
                        return Mono.error(new BusinessException(BusinessException.Type.DISH_NOT_FROM_OWNER));
                    }
                    // Busca el plato por su id
                    return dishGateway.findDishById(dishDto.getId())
                            .flatMap(dishData -> {
                                // Si el plato no existe, devuelve un error
                                if (!dishData.isPresent()) {
                                    return Mono.error(new BusinessException(BusinessException.Type.ERROR_DATABASE_DISH_NOT_FOUND));
                                }
                                // Si el plato existe, cambia su estado
                                return dishGateway.createDish(dishData.get()
                                        .toBuilder()
                                        .active(dishDto.getActive())
                                        .build());
                            });
                });
    }


    /**
     * Método para listar todos los platos por restaurante.
     *
     * @param pageNumber  numero de pagina
     * @param pageSize   tamaño de pagina
     * @return listado de PlateResponseDto
     */
    public Mono<Page<PlateResponseDto>> getAllDishes(int pageNumber, int pageSize) {
        // Obtiene todos los restaurantes
        return restaurantGateway.getRestaurants(pageNumber, pageSize)
                .flatMapMany(restaurantsPage -> Flux.fromIterable(restaurantsPage.getContent()))
                // Por cada restaurante, busca sus platos
                .flatMap(restaurantData -> dishGateway.findDishByRestaurant(restaurantData.getId())
                        // Convierte los datos del plato a un DTO de respuesta
                        .map(DataMapper::convertDishDataToDishResponseDto)
                        // Agrega los platos a la lista de platos del restaurante
                        .map(plateResponseDto -> plateResponseDto.toBuilder()
                                .dishes(dishGateway.listAllRestaurants(restaurantData.getId()))
                                .build())).collectList()
                // Devuelve una página con todos los platos
                .map(dishResponseDtos -> new Page<>(dishResponseDtos, dishResponseDtos.stream().count()));
    }

}
