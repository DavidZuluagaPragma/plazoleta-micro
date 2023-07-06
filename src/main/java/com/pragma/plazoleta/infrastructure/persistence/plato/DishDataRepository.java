package com.pragma.plazoleta.infrastructure.persistence.plato;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishDataRepository extends CrudRepository<DishData, Integer> {


    List<DishData> findAllByRestaurantId(Integer restauranteId);
}
