package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.dto.CategoryDto;
import com.pragma.plazoleta.domain.model.category.Category;
import com.pragma.plazoleta.domain.model.category.gateway.CategoryGateway;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import com.pragma.plazoleta.infrastructure.persistence.categoria.CategoryData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CategoryUseCaseTest {

    @InjectMocks
    CategoryUseCase categoryUseCase;

    @Mock
    CategoryGateway categoryGateway;

    @Test
    void createCategory() {
        Category expectedCategory = Category.builder()
                .name("FAST FOOD")
                .description("FAST FOOD")
                .build();

        CategoryDto categoryDto = CategoryDto.builder()
                .name("FAST FOOD")
                .description("FAST FOOD")
                .build();

        CategoryData categoryData = CategoryData.builder()
                .name("FAST FOOD")
                .description("FAST FOOD")
                .build();
        Mockito.when(categoryGateway.createCategory(categoryData)).thenReturn(Mono.just(expectedCategory));
        var result =  categoryUseCase.createCategory(categoryDto);
        StepVerifier.create(result)
                .expectNext(expectedCategory)
                .expectComplete()
                .verify();
    }
    @Test
    void getCategoryById() {
        var categoryData = CategoryData.builder()
                .name("FAST FOOD")
                .description("FAST FOOD")
                .build();
        var expectedCategory = Category.builder()
                .name("FAST FOOD")
                .description("FAST FOOD")
                .build();
        Mockito.when(categoryGateway.getCategoryById("1")).thenReturn(Mono.just(Optional.of(categoryData)));
        var result =  categoryUseCase.getCategoryById("1");
        StepVerifier.create(result)
                .expectNext(expectedCategory)
                .expectComplete()
                .verify();
    }
    @Test
    void getCategoryByIdError() {
        Mockito.when(categoryGateway.getCategoryById("1")).thenReturn(Mono.just(Optional.empty()));
        var result =  categoryUseCase.getCategoryById("1");
        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.ERROR_DATABASE_CATEGORY_NOT_FOUND.getMessage())
                .verify();
    }
}