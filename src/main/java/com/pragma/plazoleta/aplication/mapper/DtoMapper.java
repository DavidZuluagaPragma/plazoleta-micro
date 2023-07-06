package com.pragma.plazoleta.aplication.mapper;

import com.pragma.plazoleta.aplication.dto.CategoryDto;
import com.pragma.plazoleta.aplication.dto.DishDto;
import com.pragma.plazoleta.aplication.dto.RestaurantDto;
import com.pragma.plazoleta.aplication.dto.RestaurantResponseDto;
import com.pragma.plazoleta.domain.model.category.Category;
import com.pragma.plazoleta.domain.model.dish.Dish;
import com.pragma.plazoleta.domain.model.restaurant.Restaurant;
import com.pragma.plazoleta.infrastructure.persistence.restaurante.RestaurantData;

public class DtoMapper {

    private DtoMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Restaurant convertRestaurantDtoToRestaurant(RestaurantDto restaurantDto) {
        return Restaurant.builder()
                .id(restaurantDto.getId())
                .name(restaurantDto.getName())
                .address(restaurantDto.getAddress())
                .ownerId(restaurantDto.getOwnerId())
                .nit(restaurantDto.getNit())
                .phone(restaurantDto.getPhone())
                .logoUrl(restaurantDto.getLogoUrl())
                .build();
    }
    public static RestaurantDto convertRestaurantToRestaurantDto(Restaurant restaurant) {
        return RestaurantDto.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .ownerId(restaurant.getOwnerId())
                .nit(restaurant.getNit())
                .phone(restaurant.getPhone())
                .logoUrl(restaurant.getLogoUrl())
                .build();
    }
    public static Dish convertDishDtoToDish(DishDto dishDto) {
        return Dish.builder()
                .id(dishDto.getId())
                .name(dishDto.getName())
                .description(dishDto.getDescription())
                .active(dishDto.getActive())
                .price(dishDto.getPrice())
                .imageUrl(dishDto.getImageUrl())
                .build();
    }
    public static Category convertCategoryDtoToCategory(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .description(categoryDto.getDescription())
                .build();
    }
    public static RestaurantResponseDto convertRestaurantDataToRestaurantResponse(RestaurantData restaurantData) {
        return RestaurantResponseDto.builder()
                .name(restaurantData.getName())
                .logoUrl(restaurantData.getLogoUrl())
                .build();
    }

}
