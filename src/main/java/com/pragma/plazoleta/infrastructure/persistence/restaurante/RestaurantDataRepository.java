package com.pragma.plazoleta.infrastructure.persistence.restaurante;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantDataRepository extends JpaRepository<RestaurantData, Integer> {
}
