package com.pragma.plazoleta.aplication.mapper;

import com.pragma.plazoleta.aplication.dto.CategoryDto;
import com.pragma.plazoleta.aplication.dto.PlateResponseDto;
import com.pragma.plazoleta.aplication.dto.RestaurantResponseDto;
import com.pragma.plazoleta.domain.model.category.Category;
import com.pragma.plazoleta.domain.model.order.Order;
import com.pragma.plazoleta.domain.model.order_dish.DishOrder;
import com.pragma.plazoleta.domain.model.dish.Dish;
import com.pragma.plazoleta.domain.model.restaurant.Restaurant;
import com.pragma.plazoleta.infrastructure.persistence.categoria.CategoryData;
import com.pragma.plazoleta.infrastructure.persistence.pedido.OrderData;
import com.pragma.plazoleta.infrastructure.persistence.pedido_plato.DishOrderData;
import com.pragma.plazoleta.infrastructure.persistence.plato.DishData;
import com.pragma.plazoleta.infrastructure.persistence.restaurante.RestaurantData;

public class DataMapper {

    private DataMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Restaurant convertRestaurantDataToRestaurant(RestaurantData restaurantData) {
        return Restaurant.builder()
                .id(restaurantData.getId())
                .name(restaurantData.getName())
                .address(restaurantData.getAddress())
                .ownerId(restaurantData.getOwnerId())
                .nit(restaurantData.getNit())
                .phone(restaurantData.getPhone())
                .logoUrl(restaurantData.getLogoUrl())
                .build();
    }
    public static RestaurantData convertRestaurantToRestaurantData(Restaurant restaurant) {
        return RestaurantData.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .ownerId(restaurant.getOwnerId())
                .nit(restaurant.getNit())
                .phone(restaurant.getPhone())
                .logoUrl(restaurant.getLogoUrl())
                .build();
    }
    public static Category convertCategoryDataToCategory(CategoryData categoryData) {
        return Category.builder()
                .id(categoryData.getId())
                .name(categoryData.getName())
                .description(categoryData.getDescription())
                .build();
    }
    public static CategoryData convertCategoryToCategoryData(Category category) {
        return CategoryData.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
    public static Dish convertDishDataToDish(DishData dishData) {
        return Dish.builder()
                .id(dishData.getId())
                .name(dishData.getName())
                .description(dishData.getDescription())
                .active(dishData.getActive())
                .categoryId(dishData.getCategory().getId())
                .restaurantId(dishData.getRestaurant().getId())
                .price(dishData.getPrice())
                .imageUrl(dishData.getImageUrl())
                .build();
    }
    public static PlateResponseDto convertDishDataToDishResponseDto(DishData dish) {
        return PlateResponseDto.builder()
                .categoryDto(CategoryDto
                        .builder()
                        .id(dish.getCategory().getId())
                        .name(dish.getCategory().getName())
                        .description(dish.getCategory().getDescription())
                        .build())
                .restaurantDto(RestaurantResponseDto
                        .builder()
                        .name(dish.getRestaurant().getName())
                        .logoUrl(dish.getRestaurant().getLogoUrl())
                        .build())
                .build();
    }

    public static Order convertOrderDataToOrder(OrderData orderData) {
        return Order.builder()
                .id(orderData.getId())
                .status(orderData.getStatus())
                .chefId(orderData.getChefId())
                .clientId(orderData.getClientId())
                .date(orderData.getDate())
                .restaurantId(orderData.getRestaurantId())
                .build();
    }
    public static OrderData convertOrderToOrderData(Order order) {
        return OrderData.builder()
                .id(order.getId())
                .status(order.getStatus())
                .chefId(order.getChefId())
                .clientId(order.getClientId())
                .date(order.getDate())
                .restaurantId(order.getRestaurantId())
                .build();
    }
    public static DishOrder convertDishOrderDataToDishOrder(DishOrderData dishOrderData) {
        return DishOrder.builder()
                .dishId(dishOrderData.getDishId())
                .orderId(dishOrderData.getOrderId())
                .amount(dishOrderData.getAmount())
                .build();
    }
    public static DishOrderData convertDishOrderToDishOrderData(DishOrder dishOrder) {
        return DishOrderData.builder()
                .id(dishOrder.getId())
                .dishId(dishOrder.getDishId())
                .orderId(dishOrder.getOrderId())
                .amount(dishOrder.getAmount())
                .build();
    }

}
