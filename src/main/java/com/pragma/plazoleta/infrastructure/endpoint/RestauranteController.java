package com.pragma.plazoleta.infrastructure.endpoint;

import com.pragma.plazoleta.aplication.mapper.DtoMapper;
import com.pragma.plazoleta.aplication.dto.RestauranteDto;
import com.pragma.plazoleta.domain.usecase.RestauranteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/restaurates")
@RequiredArgsConstructor
public class RestauranteController {

    @Autowired
    RestauranteUseCase useCase;

    @PostMapping("/crear")
    public Mono<RestauranteDto> crearRestaurante(@RequestBody RestauranteDto restauranteDto){
        return useCase.crearRestaurante(DtoMapper.convertirRestauranteDtoARestaurante(restauranteDto))
                .map(DtoMapper::convertirRestauranteARestauranteDto);
    }

}
