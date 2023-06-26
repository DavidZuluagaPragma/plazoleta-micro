package com.pragma.plazoleta.domain.model.pedido_plato;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PedidoPlato {
    private Integer id;
    private Integer pedidoId;
    private Integer platoId;
    private Integer cantidad;
}
