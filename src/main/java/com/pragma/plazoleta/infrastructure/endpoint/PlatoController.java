package com.pragma.plazoleta.infrastructure.endpoint;

import com.pragma.plazoleta.aplication.dto.PlatoDto;
import com.pragma.plazoleta.aplication.dto.PlatoEditarDto;
import com.pragma.plazoleta.domain.model.plato.Plato;
import com.pragma.plazoleta.domain.usecase.PlatoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/platos")
@RequiredArgsConstructor
public class PlatoController {

    @Autowired
    PlatoUseCase useCase;

    @PostMapping("/crear")
    public Mono<Plato> crearPlato(@RequestBody PlatoDto platoDto){
        return useCase.crearPlato(platoDto, platoDto.getUsuarioId());
    }

    @PutMapping("/editar")
    public Mono<Plato> editarPlato(@RequestBody PlatoEditarDto platoDto){
        return useCase.editar(platoDto, platoDto.getUsuarioId());
    }

}
