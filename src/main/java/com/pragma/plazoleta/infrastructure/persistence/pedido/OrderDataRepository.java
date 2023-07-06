package com.pragma.plazoleta.infrastructure.persistence.pedido;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDataRepository extends CrudRepository<OrderData, Integer> {
    Optional<OrderData> findByClientIdAndStatusIsLike(Integer clienteId, String estado);

    @Query(value = "SELECT MAX(id) FROM pedido", nativeQuery = true)
    Optional<Integer> getMaxId();

    List<OrderData> findAllByStatus(String estado);
    Optional<OrderData> findByChefIdIsLike(Integer chefId);
}
