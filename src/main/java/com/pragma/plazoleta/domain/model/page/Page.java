package com.pragma.plazoleta.domain.model.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Page<T> {

    private List<T> content;

    private Long totalElements;

    public <R> Page<R> map(Function<? super T, R> mapper) {
        return Page.<R>builder()
                .content(content.stream().map(mapper).collect(Collectors.toList()))
                .totalElements(totalElements)
                .build();
    }

}
