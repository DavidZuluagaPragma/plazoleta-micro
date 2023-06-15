package com.pragma.plazoleta.aplication.mapper;

import com.pragma.plazoleta.aplication.dto.RestauranteDto;
import com.pragma.plazoleta.domain.model.restaurante.Restaurante;

public class DtoMapper {

    private DtoMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Restaurante convertirRestauranteDtoARestaurante(RestauranteDto restauranteDto) {
        return Restaurante.builder()
                .id(restauranteDto.getId())
                .nombre(restauranteDto.getNombre())
                .direccion(restauranteDto.getDireccion())
                .idPropietario(restauranteDto.getIdPropietario())
                .nit(restauranteDto.getNit())
                .telefono(restauranteDto.getTelefono())
                .urlLogo(restauranteDto.getUrlLogo())
                .build();
    }

    public static RestauranteDto convertirRestauranteARestauranteDto(Restaurante restaurante) {
        return RestauranteDto.builder()
                .id(restaurante.getId())
                .nombre(restaurante.getNombre())
                .direccion(restaurante.getDireccion())
                .idPropietario(restaurante.getIdPropietario())
                .nit(restaurante.getNit())
                .telefono(restaurante.getTelefono())
                .urlLogo(restaurante.getUrlLogo())
                .build();
    }

}
