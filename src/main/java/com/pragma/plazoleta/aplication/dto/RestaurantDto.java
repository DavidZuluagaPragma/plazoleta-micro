package com.pragma.plazoleta.aplication.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class RestaurantDto {
    private Integer id;
    @NonNull
    private String name;
    @NonNull
    private String address;
    @NonNull
    private String phone;
    @NonNull
    private String logoUrl;
    @NonNull
    private String nit;
    @NonNull
    private Integer ownerId;
}
