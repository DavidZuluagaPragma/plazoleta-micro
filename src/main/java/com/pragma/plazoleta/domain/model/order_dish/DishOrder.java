package com.pragma.plazoleta.domain.model.order_dish;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DishOrder {
    private Integer id;
    private Integer orderId;
    private Integer dishId;
    private Integer amount;
}
