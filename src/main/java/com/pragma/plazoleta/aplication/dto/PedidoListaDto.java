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

    Usuario chef;
    Usuario cliente;
    String estado;
    List<PlatoResponseDto> listaDePlatos;

}
