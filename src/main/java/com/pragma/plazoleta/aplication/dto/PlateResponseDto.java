package com.pragma.plazoleta.aplication.dto;

import com.pragma.plazoleta.domain.model.dish.Dish;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class PlateResponseDto {
    RestaurantResponseDto restaurantDto;
    CategoryDto categoryDto;
    List<Dish> dishes;
}
