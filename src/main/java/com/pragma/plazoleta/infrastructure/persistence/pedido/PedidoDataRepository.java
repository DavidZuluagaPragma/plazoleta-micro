package com.pragma.plazoleta.infrastructure.persistence.pedido;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoDataRepository extends CrudRepository<PedidoData, Integer> {
    Optional<PedidoData> findByClienteIdAndEstadoIsLike(Integer clienteId, String estado);

    @Query(value = "SELECT MAX(id) FROM pedido", nativeQuery = true)
    Optional<Long> getMaxId();

    List<PedidoData> findAllByEstado(String estado);
    Optional<PedidoData> findByChefIdIsLike(Integer chefId);
}
