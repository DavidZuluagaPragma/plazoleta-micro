package com.pragma.plazoleta.infrastructure.endpoint;

import com.pragma.plazoleta.aplication.dto.PlatoCambiarEstadoDto;
import com.pragma.plazoleta.aplication.dto.PlatoDto;
import com.pragma.plazoleta.aplication.dto.PlatoEditarDto;
import com.pragma.plazoleta.aplication.dto.PlatoRespuestaDto;
import com.pragma.plazoleta.domain.model.page.Page;
import com.pragma.plazoleta.domain.model.plato.Plato;
import com.pragma.plazoleta.domain.usecase.PlatoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/platos")
@RequiredArgsConstructor
public class PlatoController {

    @Autowired
    PlatoUseCase useCase;

    @PostMapping("/crear")
    @PreAuthorize("hasAuthority('PROPETARIO')")
    public Mono<Plato> crearPlato(@RequestBody PlatoDto platoDto){
        return useCase.crearPlato(platoDto, platoDto.getUsuarioId());
    }

    @PutMapping("/editar")
    public Mono<Plato> editarPlato(@RequestBody PlatoEditarDto platoDto){
        return useCase.editar(platoDto, platoDto.getUsuarioId());
    }

    @PutMapping("/editar-estado")
    @PreAuthorize("hasAuthority('PROPETARIO')")
    public Mono<Plato> cambiarEstadoPlato(Principal principal, @RequestBody PlatoCambiarEstadoDto platoDto){
        return useCase.cambiarEstadoPlato(platoDto, principal.getName());
    }

    @GetMapping("/todos")
    public Mono<Page<PlatoRespuestaDto>> conseguirTodosLosPlatos(@RequestParam(defaultValue = "0") int numeroPagina, @RequestParam(defaultValue = "10") int tamanoPagina){
        return useCase.conseguirTodosLosPlatos(numeroPagina,tamanoPagina);
    }

}
