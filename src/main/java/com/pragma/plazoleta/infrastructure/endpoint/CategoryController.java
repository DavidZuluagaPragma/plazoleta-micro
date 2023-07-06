package com.pragma.plazoleta.infrastructure.endpoint;

import com.pragma.plazoleta.aplication.dto.CategoryDto;
import com.pragma.plazoleta.domain.model.category.Category;
import com.pragma.plazoleta.domain.usecase.CategoryUseCase;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    @Autowired
    CategoryUseCase useCase;

    @PostMapping("/create")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Mono<Category> createCategory(@RequestBody CategoryDto categoryDto){
        return useCase.createCategory(categoryDto);
    }

}
