package com.pragma.plazoleta.infrastructure.endpoint;

import com.pragma.plazoleta.aplication.dto.*;
import com.pragma.plazoleta.domain.model.page.Page;
import com.pragma.plazoleta.domain.usecase.PedidoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


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

    @GetMapping("/listar/{estado}")
    @PreAuthorize("hasAuthority('EMPLEADO')")
    public Mono<Page<PedidoListaDto>> listarPedidosPorEstado(@RequestHeader("Authorization") String token, @PathVariable String estado){
        return useCase.listarPedidosPorEstado(estado, token);
    }

}
