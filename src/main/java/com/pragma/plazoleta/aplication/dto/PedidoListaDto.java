package com.pragma.plazoleta.aplication.dto;

import com.pragma.plazoleta.domain.model.usuario.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class PedidoListaDto {

    private Integer id;
    private Usuario chef;
    private Usuario cliente;
    private String estado;
    private List<PlatoResponseDto> listaDePlatos;

}
