package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.dto.CategoryDto;
import com.pragma.plazoleta.aplication.mapper.DataMapper;
import com.pragma.plazoleta.aplication.mapper.DtoMapper;
import com.pragma.plazoleta.domain.model.category.Category;
import com.pragma.plazoleta.domain.model.category.gateway.CategoryGateway;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class CategoryUseCase {

    @Autowired
    CategoryGateway categoryGateway;

    /**
     * Método para crear categoria.
     *
     * @param categoryDto objeto con los datos necesarios para crear la categoria
     * @return la categoria creada
     */
    public Mono<Category> createCategory(CategoryDto categoryDto) {
        return Mono.fromCallable( () -> DtoMapper.convertCategoryDtoToCategory(categoryDto))
                .flatMap( category -> categoryGateway.createCategory(DataMapper.convertCategoryToCategoryData(category)));
    }

    /**
     * Método para conseguir la categoria por ID.
     *
     * @param categoryId id de la categoria
     * @return la categoria encontrada
     */
    public Mono<Category> getCategoryById(String categoryId) {
        return categoryGateway.getCategoryById(categoryId)
                .flatMap(existingCategory -> {
                    if (!existingCategory.isPresent()) {
                        return Mono.error(new BusinessException(BusinessException.Type.ERROR_DATABASE_CATEGORY_NOT_FOUND));
                    }
                    return Mono.just(DataMapper.convertCategoryDataToCategory(existingCategory.get()));
                });
    }
}
