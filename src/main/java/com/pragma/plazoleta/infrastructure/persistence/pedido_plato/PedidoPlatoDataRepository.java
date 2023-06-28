package com.pragma.plazoleta.infrastructure.persistence.pedido_plato;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PedidoPlatoDataRepository extends CrudRepository<PedidoPlatoData, Integer> {
    List<PedidoPlatoData> findAllByPedidoId(Integer pedidoId);
}
