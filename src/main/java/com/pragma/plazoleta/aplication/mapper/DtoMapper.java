package com.pragma.plazoleta.aplication.mapper;

import com.pragma.plazoleta.aplication.dto.CategoriaDto;
import com.pragma.plazoleta.aplication.dto.PlatoDto;
import com.pragma.plazoleta.aplication.dto.RestauranteDto;
import com.pragma.plazoleta.aplication.dto.RestauranteRespuestaDto;
import com.pragma.plazoleta.domain.model.categoria.Categoria;
import com.pragma.plazoleta.domain.model.plato.Plato;
import com.pragma.plazoleta.domain.model.restaurante.Restaurante;
import com.pragma.plazoleta.infrastructure.persistence.plato.PlatoData;
import com.pragma.plazoleta.infrastructure.persistence.restaurante.RestauranteData;

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

    public static Plato convertirPlatoDtoAPlato(PlatoDto platoDto) {
        return Plato.builder()
                .id(platoDto.getId())
                .nombre(platoDto.getNombre())
                .descripcion(platoDto.getDescripcion())
                .activo(platoDto.getActivo())
                .precio(platoDto.getPrecio())
                .urlImagen(platoDto.getUrlImagen())
                .build();
    }

    public static Categoria convertirCategoriaDtoACategoria(CategoriaDto categoriaDto) {
        return Categoria.builder()
                .Id(categoriaDto.getId())
                .Nombre(categoriaDto.getNombre())
                .Descripcion(categoriaDto.getDescripcion())
                .build();
    }

    public static RestauranteRespuestaDto convertirRestauranteDataARestauranteRespuesta(RestauranteData restauranteData) {
        return RestauranteRespuestaDto.builder()
                .nombre(restauranteData.getNombre())
                .urlLogo(restauranteData.getUrlLogo())
                .build();
    }

}
