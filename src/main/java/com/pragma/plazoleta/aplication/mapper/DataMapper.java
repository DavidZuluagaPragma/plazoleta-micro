package com.pragma.plazoleta.aplication.mapper;

import com.pragma.plazoleta.domain.model.restaurante.Restaurante;
import com.pragma.plazoleta.infrastructure.persistence.RestauranteData;

public class DataMapper {

    private DataMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Restaurante convertirRestauranteDataARestaurante(RestauranteData restauranteData) {
        return Restaurante.builder()
                .id(restauranteData.getId())
                .nombre(restauranteData.getNombre())
                .direccion(restauranteData.getDireccion())
                .idPropietario(restauranteData.getIdPropietario())
                .nit(restauranteData.getNit())
                .telefono(restauranteData.getTelefono())
                .urlLogo(restauranteData.getUrlLogo())
                .build();
    }

    public static RestauranteData convertirRestauranteARestauranteData(Restaurante restaurante) {
        return RestauranteData.builder()
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
