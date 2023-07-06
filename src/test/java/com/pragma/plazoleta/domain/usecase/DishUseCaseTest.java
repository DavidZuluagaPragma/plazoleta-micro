package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.dto.*;
import com.pragma.plazoleta.domain.model.category.Category;
import com.pragma.plazoleta.domain.model.dish.Dish;
import com.pragma.plazoleta.domain.model.dish.gateway.DishGateway;
import com.pragma.plazoleta.domain.model.restaurant.Restaurant;
import com.pragma.plazoleta.domain.model.restaurant.gateway.RestaurantGateway;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import com.pragma.plazoleta.infrastructure.persistence.categoria.CategoryData;
import com.pragma.plazoleta.infrastructure.persistence.plato.DishData;
import com.pragma.plazoleta.infrastructure.persistence.restaurante.RestaurantData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DishUseCaseTest {

    @InjectMocks
    DishUseCase dishUseCase;

    @Mock
    RestaurantUseCase restaurantUseCase;

    @Mock
    CategoryUseCase categoryUseCase;

    @Mock
    DishGateway dishGateway;

    @Mock
    RestaurantGateway restaurantGateway;

    @Test
    void createDish() {
        Restaurant expectedRestaurant = Restaurant.builder()
                .name("PAPANATAS")
                .address("LOS PATIOS")
                .phone("+573202040834")
                .logoUrl("urlLogo")
                .nit("123456")
                .ownerId(1)
                .build();
        Category expectedCategory = Category.builder()
                .id(1)
                .name("FAST FOOD")
                .description("FAST FOOD")
                .build();
        CategoryData category = CategoryData.builder()
                .id(1)
                .name("FAST FOOD")
                .description("FAST FOOD")
                .build();
        RestaurantData restaurant = RestaurantData.builder()
                .id(1)
                .name("PAPANATAS")
                .address("LOS PATIOS")
                .phone("+573202040834")
                .logoUrl("urlLogo")
                .nit("123456")
                .ownerId(1)
                .build();
        DishDto newDish = DishDto.builder()
                .price(100.0)
                .imageUrl("url")
                .name("PLATTER")
                .description("DISH")
                .active(Boolean.TRUE)
                .categoryId(category.getId())
                .restaurantId(restaurant.getId())
                .build();
        Dish expectedDish = Dish.builder()
                .price(100.0)
                .imageUrl("url")
                .name("PLATTER")
                .description("DISH")
                .active(Boolean.TRUE)
                .categoryId(1)
                .restaurantId(1)
                .build();
        DishDto dishDto = DishDto.builder()
                .price(100.0)
                .imageUrl("url")
                .name("PLATTER")
                .description("DISH")
                .active(Boolean.TRUE)
                .categoryId(1)
                .restaurantId(1)
                .userId("1")
                .build();
        Mockito.when(restaurantUseCase.isOwner("1", "TOKEN")).thenReturn(Mono.just(Boolean.TRUE));
        /*Mockito.when(restaurantUseCase.checkRestaurantExists("1")).thenReturn(Mono.just(expectedRestaurant));
        Mockito.when(categoryUseCase.getCategoryById("1")).thenReturn(Mono.just(expectedCategory));*/
        Mockito.when(dishUseCase.createDish(newDish, dishDto.getUserId(), "TOKEN")).thenReturn(Mono.just(expectedDish));

        var result = dishUseCase.createDish(dishDto, dishDto.getUserId(), "TOKEN");
        StepVerifier.create(result)
                .expectNext(expectedDish)
                .expectComplete();
    }
    @Test
    void createDishErrorNotOwner() {
        var dishDto = DishDto.builder()
                .price(100.0)
                .imageUrl("url")
                .name("PLATTER")
                .description("DISH")
                .active(Boolean.TRUE)
                .categoryId(1)
                .restaurantId(1)
                .userId("1")
                .build();
        Mockito.when(restaurantUseCase.isOwner("1", "TOKEN")).thenReturn(Mono.just(Boolean.FALSE));
        var result = dishUseCase.createDish(dishDto, dishDto.getUserId(), "TOKEN");
        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.USER_NOT_OWNER.getMessage())
                .verify();
    }

    @Test
    void editDish() {
        CategoryData category = CategoryData.builder()
                .id(1)
                .name("FAST FOOD")
                .description("FAST FOOD")
                .build();
        RestaurantData restaurant = RestaurantData.builder()
                .name("PAPANATAS")
                .address("LOS PATIOS")
                .phone("+573202040834")
                .logoUrl("urlLogo")
                .nit("123456")
                .ownerId(1)
                .build();
        DishData foundDish = DishData.builder()
                .price(100.0)
                .imageUrl("url")
                .name("PICADA")
                .description("DISH")
                .active(Boolean.TRUE)
                .category(category)
                .restaurant(restaurant)
                .build();
        DishEditDto dishDto = DishEditDto.builder()
                .id(1)
                .description("DESCRIPTION")
                .price(2000.0)
                .build();
        Dish editedDish = Dish.builder()
                .imageUrl("url")
                .name("PICADA")
                .active(Boolean.TRUE)
                .categoryId(category.getId())
                .restaurantId(restaurant.getId())
                .price(dishDto.getPrice())
                .description(dishDto.getDescription())
                .build();
        Mockito.when(restaurantUseCase.isOwner("1", "TOKEN")).thenReturn(Mono.just(Boolean.TRUE));
        Mockito.when(dishGateway.findDishById(1)).thenReturn(Mono.just(Optional.of(foundDish)));
        Mockito.when(dishGateway.createDish(foundDish.toBuilder()
                .price(dishDto.getPrice())
                .description(dishDto.getDescription())
                .build())).thenReturn(Mono.just(editedDish));
        var result = dishUseCase.edit(dishDto, restaurant.getOwnerId().toString(), "TOKEN");
        StepVerifier.create(result)
                .expectNext(editedDish)
                .expectComplete()
                .verify();
    }
    @Test
    void editDishErrorNotOwner() {
        DishEditDto dishDto = DishEditDto.builder()
                .id(1)
                .description("DESCRIPTION")
                .price(2000.0)
                .build();
        Mockito.when(restaurantUseCase.isOwner("1", "TOKEN")).thenReturn(Mono.just(Boolean.FALSE));
        var result = dishUseCase.edit(dishDto, "1", "TOKEN");
        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.USER_NOT_OWNER.getMessage())
                .verify();
    }
    @Test
    void editDishErrorIdIsNull() {
        DishEditDto dishDto = DishEditDto.builder()
                .price(100.0)
                .description("DISH")
                .build();
        Mockito.when(restaurantUseCase.isOwner("1", "TOKEN")).thenReturn(Mono.just(Boolean.TRUE));
        var result = dishUseCase.edit(dishDto, "1", "TOKEN");
        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.EDIT_DISH_ERROR.getMessage())
                .verify();
    }
    @Test
    void editDishErrorDishNotFound() {
        DishEditDto dishDto = DishEditDto.builder()
                .id(1)
                .price(100.0)
                .description("DISH")
                .build();
        Mockito.when(restaurantUseCase.isOwner("1", "TOKEN")).thenReturn(Mono.just(Boolean.TRUE));
        Mockito.when(dishGateway.findDishById(1)).thenReturn(Mono.just(Optional.empty()));
        var result = dishUseCase.edit(dishDto, "1", "TOKEN");
        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.ERROR_DATABASE_DISH_NOT_FOUND.getMessage())
                .verify();
    }
    @Test
    void getAllDishesSuccessful() {
        List<RestaurantData> restaurantDataList = new ArrayList<>();
        RestaurantData restaurant = RestaurantData.builder()
                .name("PAPANATAS")
                .address("LOS PATIOS")
                .phone("+573202040834")
                .logoUrl("urlLogo")
                .nit("123456")
                .ownerId(1)
                .build();
        restaurantDataList.add(restaurant);
        int pageNumber = 0;
        int pageSize = 10;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        List<DishData> dishDataList = new ArrayList<>();
        CategoryData category = CategoryData.builder()
                .id(1)
                .name("FAST FOOD")
                .description("FAST FOOD")
                .build();
        Dish dish = Dish.builder()
                .price(100.0)
                .imageUrl("url")
                .name("PICADA")
                .description("DISH")
                .active(Boolean.TRUE)
                .categoryId(1)
                .restaurantId(restaurant.getId())
                .build();
        DishData foundDish = DishData.builder()
                .price(100.0)
                .imageUrl("url")
                .name("PICADA")
                .description("DISH")
                .active(Boolean.TRUE)
                .category(category)
                .restaurant(restaurant)
                .build();
        PlateResponseDto plateResponseDto = PlateResponseDto
                .builder()
                .dishes(List.of(dish))
                .categoryDto(CategoryDto.builder()
                        .id(1)
                        .name(category.getName())
                        .description(category.getDescription())
                        .build())
                .restaurantDto(RestaurantResponseDto.builder()
                        .name("PAPANATAS")
                        .logoUrl("urlLogo")
                        .build())
                .build();
        dishDataList.add(foundDish);
        Page<RestaurantData> restaurantPage = new PageImpl<>(restaurantDataList, pageRequest, restaurantDataList.size());
        com.pragma.plazoleta.domain.model.page.Page expectedPage = com.pragma.plazoleta.domain.model.page.Page.builder()
                .content(List.of(plateResponseDto))
                .totalElements(dishDataList.stream().count())
                .build();
        Mockito.when(restaurantGateway.getRestaurants(1, 10)).thenReturn(Mono.just(restaurantPage));
        Mockito.when(dishGateway.findDishByRestaurant(restaurant.getId())).thenReturn(Flux.just(foundDish));
        Mockito.when(dishGateway.listAllRestaurants(restaurant.getId())).thenReturn(List.of(dish));
        var result = dishUseCase.getAllDishes(1, 10);
        StepVerifier.create(result)
                .expectNext(expectedPage)
                .expectComplete()
                .verify();
    }

}