package com.pragma.plazoleta.infrastructure.endpoint;

import com.pragma.plazoleta.aplication.dto.CategoriaDto;
import com.pragma.plazoleta.aplication.mapper.DtoMapper;
import com.pragma.plazoleta.domain.model.categoria.Categoria;
import com.pragma.plazoleta.domain.usecase.CategoriaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    @Autowired
    CategoriaUseCase useCase;

    @PostMapping("/crear")
    public Mono<Categoria> crearCategoria(@RequestBody CategoriaDto categoriaDto){
        return useCase.crearCategoria(DtoMapper.convertirCategoriaDtoACategoria(categoriaDto));
    }

}
