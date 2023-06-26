package com.pragma.plazoleta.infrastructure.persistence.plato;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatoDataRepository extends CrudRepository<PlatoData, Integer> {


    List<PlatoData> findAllByRestauranteId(Integer restauranteId);
}
