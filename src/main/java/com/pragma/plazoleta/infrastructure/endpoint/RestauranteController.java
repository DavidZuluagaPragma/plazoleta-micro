package com.pragma.plazoleta.infrastructure.endpoint;

import com.pragma.plazoleta.aplication.dto.RestauranteRespuestaDto;
import com.pragma.plazoleta.aplication.mapper.DtoMapper;
import com.pragma.plazoleta.aplication.dto.RestauranteDto;
import com.pragma.plazoleta.domain.model.page.Page;
import com.pragma.plazoleta.domain.usecase.RestauranteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/restaurates")
@RequiredArgsConstructor
public class RestauranteController {

    @Autowired
    RestauranteUseCase useCase;

    @PostMapping("/crear")
    public Mono<RestauranteDto> crearRestaurante(@RequestBody RestauranteDto restauranteDto, @RequestHeader("Authorization") String token){
        return useCase.crearRestaurante(DtoMapper.convertirRestauranteDtoARestaurante(restauranteDto),token)
                .map(DtoMapper::convertirRestauranteARestauranteDto);
    }

    @GetMapping("/todos")
    public Mono<Page<RestauranteRespuestaDto>> conseguirRestaurantes(@RequestParam(defaultValue = "0") int numeroPagina, @RequestParam(defaultValue = "10") int tamanoPagina){
        return useCase.conseguirRestaurantes(numeroPagina, tamanoPagina);
    }

}
