package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.dto.RestaurantDto;
import com.pragma.plazoleta.aplication.dto.RestaurantResponseDto;
import com.pragma.plazoleta.domain.model.restaurant.Restaurant;
import com.pragma.plazoleta.domain.model.restaurant.gateway.RestaurantGateway;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class RestaurantUseCaseTest {

    @Mock
    RestaurantGateway restaurantGateway;

    @InjectMocks
    RestaurantUseCase restaurantUseCase;

    @Test
    void createRestaurant() {
        RestaurantData restaurantData= RestaurantData.builder()
                .name("PAPANATAS")
                .address("LOS PATIOS")
                .phone("+573202040834")
                .logoUrl("urlLogo")
                .nit("123456")
                .ownerId(1)
                .build();

        Restaurant restaurant = Restaurant.builder()
                .name("PAPANATAS")
                .address("LOS PATIOS")
                .phone("+573202040834")
                .logoUrl("urlLogo")
                .nit("123456")
                .ownerId(1)
                .build();

        RestaurantDto expectedRestaurant = RestaurantDto.builder()
                .name("PAPANATAS")
                .address("LOS PATIOS")
                .phone("+573202040834")
                .logoUrl("urlLogo")
                .nit("123456")
                .ownerId(1)
                .build();
        Mockito.when(restaurantGateway.isOwner(String.valueOf(restaurantData.getOwnerId()), "TOKEN")).thenReturn(Mono.just(Boolean.TRUE));
        Mockito.when(restaurantGateway.createRestaurant(restaurantData)).thenReturn(Mono.just(restaurant));
        var result = restaurantUseCase.createRestaurant(expectedRestaurant,"TOKEN");
        StepVerifier.create(result)
                .expectNext(expectedRestaurant)
                .expectComplete()
                .verify();
    }
    @Test
    void createRestaurantErrorOwner() {
        RestaurantData restaurant = RestaurantData.builder()
                .name("PAPANATAS")
                .address("LOS PATIOS")
                .phone("+573202040834")
                .logoUrl("urlLogo")
                .nit("123456")
                .ownerId(1)
                .build();
        RestaurantDto expectedRestaurant = RestaurantDto.builder()
                .name("PAPANATAS")
                .address("LOS PATIOS")
                .phone("+573202040834")
                .logoUrl("urlLogo")
                .nit("123456")
                .ownerId(1)
                .build();
        Mockito.when(restaurantGateway.isOwner(String.valueOf(restaurant.getOwnerId()), "TOKEN")).thenReturn(Mono.just(Boolean.FALSE));
        var result = restaurantUseCase.createRestaurant(expectedRestaurant, "TOKEN");
        StepVerifier.create(result)
                .expectErrorMessage("The user is not an owner role")
                .verify();
    }
    @Test
    void createRestaurantErrorPhone() {
        RestaurantDto expectedRestaurant = RestaurantDto.builder()
                .name("PAPANATAS")
                .address("LOS PATIOS")
                .phone("phone")
                .logoUrl("urlLogo")
                .nit("123456")
                .ownerId(1)
                .build();
        var result = restaurantUseCase.createRestaurant(expectedRestaurant,"TOKEN");
        StepVerifier.create(result)
                .expectErrorMessage("The phone number can only be numeric")
                .verify();
    }
    @Test
    void isOwnerSuccessful(){
        Mockito.when(restaurantGateway.isOwner("1","TOKEN")).thenReturn(Mono.just(Boolean.TRUE));
        var result = restaurantUseCase.isOwner("1","TOKEN");
        StepVerifier.create(result)
                .expectNext(Boolean.TRUE)
                .expectComplete()
                .verify();
    }

    @Test
    void restaurantExists(){
        var restaurant = RestaurantData.builder()
                .name("PAPANATAS")
                .address("LOS PATIOS")
                .phone("+573202040834")
                .logoUrl("urlLogo")
                .nit("123456")
                .ownerId(1)
                .build();
        var expectedRestaurant = Restaurant.builder()
                .name("PAPANATAS")
                .address("LOS PATIOS")
                .phone("+573202040834")
                .logoUrl("urlLogo")
                .nit("123456")
                .ownerId(1)
                .build();
        Mockito.when(restaurantGateway.findRestaurant("1")).thenReturn(Mono.just(Optional.of(restaurant)));
        var result = restaurantUseCase.checkRestaurantExists("1");
        StepVerifier.create(result)
                .expectNext(expectedRestaurant)
                .expectComplete()
                .verify();
    }
    @Test
    void restaurantExistsErrorEmptyData() {
        Mockito.when(restaurantGateway.findRestaurant("1")).thenReturn(Mono.just(Optional.empty()));
        var result = restaurantUseCase.checkRestaurantExists("1");
        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.ERROR_DATABASE_RESTAURANT_NOT_FOUND.getMessage())
                .verify();
    }
    @Test
    void getRestaurantsSuccessful() {
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
        Page<RestaurantData> restaurantPage = new PageImpl<>(restaurantDataList, pageRequest, restaurantDataList.size());
        List<RestaurantResponseDto> expectedRestaurantData = new ArrayList<>();
        RestaurantResponseDto restaurantResponseDto = RestaurantResponseDto.builder()
                .name("PAPANATAS")
                .logoUrl("urlLogo")
                .build();
        expectedRestaurantData.add(restaurantResponseDto);
        com.pragma.plazoleta.domain.model.page.Page expectedPage = com.pragma.plazoleta.domain.model.page.Page.builder()
                .content(Arrays.asList(expectedRestaurantData.toArray()))
                .totalElements(expectedRestaurantData.stream().count())
                .build();
        Mockito.when(restaurantGateway.getRestaurants(1, 10)).thenReturn(Mono.just(restaurantPage));
        var result = restaurantUseCase.getRestaurants(1, 10);
        StepVerifier.create(result)
                .expectNext(expectedPage)
                .expectComplete()
                .verify();
    }

}