package com.pragma.plazoleta.infrastructure.endpoint;

import com.pragma.plazoleta.aplication.dto.DishChangeStatusDto;
import com.pragma.plazoleta.aplication.dto.DishDto;
import com.pragma.plazoleta.aplication.dto.DishEditDto;
import com.pragma.plazoleta.aplication.dto.PlateResponseDto;
import com.pragma.plazoleta.domain.model.page.Page;
import com.pragma.plazoleta.domain.model.dish.Dish;
import com.pragma.plazoleta.domain.usecase.DishUseCase;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
public class DishController {

    @Autowired
    DishUseCase useCase;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('OWNER')")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Mono<Dish> createDish(@RequestBody DishDto dishDto, Principal principal, @RequestHeader("Authorization") String token) {
        return useCase.createDish(dishDto, principal.getName(), token);
    }

    @PutMapping("/edit")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Mono<Dish> editDish(@RequestBody DishEditDto dishDto, Principal principal, @RequestHeader("Authorization") String token) {
        return useCase.edit(dishDto, principal.getName(), token);
    }

    @PutMapping("/change-status")
    @PreAuthorize("hasAuthority('OWNER')")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Mono<Dish> changeDishStatus(Principal principal, @RequestBody DishChangeStatusDto dishDto) {
        return useCase.changeDishStatus(dishDto, principal.getName());
    }

    @GetMapping("/all")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Mono<Page<PlateResponseDto>> getAllDishes(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {
        return useCase.getAllDishes(pageNumber, pageSize);
    }
}
