package com.pragma.plazoleta.domain.model.category.gateway;

import com.pragma.plazoleta.domain.model.category.Category;
import com.pragma.plazoleta.infrastructure.persistence.categoria.CategoryData;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface CategoryGateway {
    Mono<Category> createCategory(CategoryData categoryData);
    Mono<Optional<CategoryData>> getCategoryById(String categoryId);
}
