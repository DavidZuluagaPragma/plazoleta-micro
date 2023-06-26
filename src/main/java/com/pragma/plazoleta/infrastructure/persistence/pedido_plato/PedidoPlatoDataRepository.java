package com.pragma.plazoleta.infrastructure.persistence.pedido_plato;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PedidoPlatoDataRepository extends CrudRepository<PedidoPlatoData, Integer> {
}
