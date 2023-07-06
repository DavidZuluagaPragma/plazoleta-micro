package com.pragma.plazoleta.infrastructure.persistence.categoria;

import com.pragma.plazoleta.aplication.mapper.DataMapper;
import com.pragma.plazoleta.domain.model.category.Category;
import com.pragma.plazoleta.domain.model.category.gateway.CategoryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class CategoryAdapterRepository implements CategoryGateway {

    @Autowired
    CategoryDataRepository repository;

    @Override
    public Mono<Category> createCategory(CategoryData categoryData) {
        return Mono.fromCallable(() -> repository.save(categoryData))
                .map(DataMapper::convertCategoryDataToCategory);
    }
    @Override
    public Mono<Optional<CategoryData>> getCategoryById(String categoryId) {
        return Mono.fromCallable(() -> repository.findById(Integer.parseInt(categoryId)))
                .onErrorResume(Mono::error);
    }
}
