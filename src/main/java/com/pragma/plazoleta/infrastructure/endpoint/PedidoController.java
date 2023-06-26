package com.pragma.plazoleta.infrastructure.endpoint;

import com.pragma.plazoleta.aplication.dto.PedidoDto;
import com.pragma.plazoleta.aplication.dto.PedidoResponse;
import com.pragma.plazoleta.domain.usecase.PedidoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    @Autowired
    PedidoUseCase useCase;

    @PostMapping("/crear")
    public Flux<PedidoResponse> crearPedido(@RequestBody PedidoDto pedidoDto){
        return useCase.crearPedido(pedidoDto);
    }

}
