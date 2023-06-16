package com.pragma.plazoleta.infrastructure.persistence.categoria;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaDataRepository extends CrudRepository<CategoriaData, Integer> {
}
