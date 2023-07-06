package com.pragma.plazoleta.infrastructure.endpoint;

import com.pragma.plazoleta.aplication.dto.RestaurantResponseDto;
import com.pragma.plazoleta.aplication.mapper.DtoMapper;
import com.pragma.plazoleta.aplication.dto.RestaurantDto;
import com.pragma.plazoleta.domain.model.page.Page;
import com.pragma.plazoleta.domain.usecase.RestaurantUseCase;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    @Autowired
    RestaurantUseCase useCase;
    @PostMapping("/create")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Mono<RestaurantDto> createRestaurant(@RequestBody RestaurantDto restaurantDto, @RequestHeader("Authorization") String token){
        return useCase.createRestaurant(restaurantDto, token);
    }
    @GetMapping("/all")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Mono<Page<RestaurantResponseDto>> getAllRestaurants(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize){
        return useCase.getRestaurants(pageNumber, pageSize);
    }
}
