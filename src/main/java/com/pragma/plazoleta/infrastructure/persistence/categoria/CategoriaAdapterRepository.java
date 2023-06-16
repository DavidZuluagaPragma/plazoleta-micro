package com.pragma.plazoleta.infrastructure.persistence.categoria;

import com.pragma.plazoleta.aplication.mapper.DataMapper;
import com.pragma.plazoleta.domain.model.categoria.Categoria;
import com.pragma.plazoleta.domain.model.categoria.gateway.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class CategoriaAdapterRepository implements CategoriaRepository {

    @Autowired
    CategoriaDataRepository repository;

    @Override
    public Mono<Categoria> crearCategoria(CategoriaData categoriaData) {
        return Mono.fromCallable(() -> repository.save(categoriaData))
                .map(DataMapper::convertirCategoriaDataACategoria);
    }

    @Override
    public Mono<Optional<CategoriaData>> conseguirCategoriaPorId(String categoriaId) {
        return Mono.fromCallable(() -> repository.findById(Integer.parseInt(categoriaId)))
                .onErrorResume(Mono::error);
    }
}
