package com.pragma.plazoleta.domain.model.dish;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Dish {
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private Boolean active;
    private Integer restaurantId;
    private Integer categoryId;
}
