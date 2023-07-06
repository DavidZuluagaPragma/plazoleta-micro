package com.pragma.plazoleta.infrastructure.persistence.plato;

import com.pragma.plazoleta.infrastructure.persistence.categoria.CategoryData;
import com.pragma.plazoleta.infrastructure.persistence.restaurante.RestaurantData;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "dish")
public class DishData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private Boolean active;
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private RestaurantData restaurant;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryData category;
}
