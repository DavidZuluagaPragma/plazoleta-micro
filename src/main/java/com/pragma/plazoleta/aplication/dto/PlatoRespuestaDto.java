package com.pragma.plazoleta.aplication.dto;

import com.pragma.plazoleta.domain.model.plato.Plato;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class PlatoRespuestaDto {
    RestauranteRespuestaDto restauranteDto;
    CategoriaDto categoriaDto;
    List<Plato> platos;
}
