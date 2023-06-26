package com.pragma.plazoleta.aplication.mapper;

import com.pragma.plazoleta.aplication.dto.CategoriaDto;
import com.pragma.plazoleta.aplication.dto.PlatoRespuestaDto;
import com.pragma.plazoleta.aplication.dto.RestauranteRespuestaDto;
import com.pragma.plazoleta.domain.model.categoria.Categoria;
import com.pragma.plazoleta.domain.model.plato.Plato;
import com.pragma.plazoleta.domain.model.restaurante.Restaurante;
import com.pragma.plazoleta.infrastructure.persistence.categoria.CategoriaData;
import com.pragma.plazoleta.infrastructure.persistence.plato.PlatoData;
import com.pragma.plazoleta.infrastructure.persistence.restaurante.RestauranteData;

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

    public static Categoria convertirCategoriaDataACategoria(CategoriaData categoriaData) {
        return Categoria.builder()
                .Id(categoriaData.getId())
                .Nombre(categoriaData.getNombre())
                .Descripcion(categoriaData.getDescripcion())
                .build();
    }

    public static CategoriaData convertirCategoriaACategoriaData(Categoria categoria) {
        return CategoriaData.builder()
                .Id(categoria.getId())
                .Nombre(categoria.getNombre())
                .Descripcion(categoria.getDescripcion())
                .build();
    }

    public static Plato convertirPlatoDataAPlato(PlatoData platoData) {
        return Plato.builder()
                .id(platoData.getId())
                .nombre(platoData.getNombre())
                .descripcion(platoData.getDescripcion())
                .activo(platoData.getActivo())
                .categoriaId(platoData.getCategoria().getId())
                .restauranteId(platoData.getRestaurante().getId())
                .precio(platoData.getPrecio())
                .urlImagen(platoData.getUrlImagen())
                .build();
    }

    public static PlatoRespuestaDto convertirPlatoDataAPlatoRespuestaDto(PlatoData plato) {
        return PlatoRespuestaDto.builder()
                .categoriaDto(CategoriaDto
                        .builder()
                        .id(plato.getCategoria().getId())
                        .nombre(plato.getCategoria().getNombre())
                        .descripcion(plato.getCategoria().getDescripcion())
                        .build())
                .restauranteDto(RestauranteRespuestaDto
                        .builder()
                        .nombre(plato.getRestaurante().getNombre())
                        .urlLogo(plato.getRestaurante().getUrlLogo())
                        .build())
                .build();
    }

}
