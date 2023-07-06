package com.pragma.plazoleta.aplication.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class CategoryDto {
    private Integer id;
    @NonNull
    private String name;
    @NonNull
    private String description;
}
