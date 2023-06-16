package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.mapper.DataMapper;
import com.pragma.plazoleta.domain.model.categoria.Categoria;
import com.pragma.plazoleta.domain.model.categoria.gateway.CategoriaRepository;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class CategoriaUseCase {

    @Autowired
    CategoriaRepository categoriaRepository;

    public Mono<Categoria> crearCategoria(Categoria categoria){
        return categoriaRepository.crearCategoria(DataMapper.convertirCategoriaACategoriaData(categoria));
    }
    public Mono<Categoria> conseguirCategoriaPorId(String categoriaId){
        return categoriaRepository.conseguirCategoriaPorId(categoriaId)
                .flatMap(existeCategoria -> {
                    if (!existeCategoria.isPresent()){
                        return Mono.error(new BusinessException(BusinessException.Type.ERROR_BASE_DATOS_CATEGORIA_NO_ENCONTRADA));
                    }
                    return Mono.just(DataMapper.convertirCategoriaDataACategoria(existeCategoria.get()));
                });
    }

}
