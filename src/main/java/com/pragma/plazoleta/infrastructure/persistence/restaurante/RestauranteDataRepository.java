package com.pragma.plazoleta.infrastructure.persistence.restaurante;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestauranteDataRepository extends CrudRepository<RestauranteData, Integer> {
}
