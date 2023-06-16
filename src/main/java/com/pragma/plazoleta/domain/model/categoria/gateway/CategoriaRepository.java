package com.pragma.plazoleta.domain.model.categoria.gateway;

import com.pragma.plazoleta.domain.model.categoria.Categoria;
import com.pragma.plazoleta.infrastructure.persistence.categoria.CategoriaData;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface CategoriaRepository {
    Mono<Categoria> crearCategoria(CategoriaData categoriaData);
    Mono<Optional<CategoriaData>> conseguirCategoriaPorId(String categoriaId);
}
