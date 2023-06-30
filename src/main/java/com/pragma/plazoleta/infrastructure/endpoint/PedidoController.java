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

import java.security.Principal;


@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    @Autowired
    PedidoUseCase useCase;

    @PostMapping("/crear")
    public Flux<PedidoResponse> crearPedido(@RequestBody PedidoDto pedidoDto) {
        return useCase.crearPedido(pedidoDto);
    }

    @GetMapping("/listar/{estado}")
    @PreAuthorize("hasAuthority('EMPLEADO')")
    public Mono<Page<PedidoListaDto>> listarPedidosPorEstado(@RequestHeader("Authorization") String token, @PathVariable String estado) {
        return useCase.listarPedidosPorEstado(estado, token);
    }

    @PutMapping("/asignar")
    @PreAuthorize("hasAuthority('EMPLEADO')")
    public Mono<PedidoListaDto> asignarPedido(@RequestHeader("Authorization") String token,
                                              Principal principal,
                                              @RequestBody AssignOrderRequestDto assignOrderRequestDto) {
        return useCase.asignarPedido(principal.getName(), assignOrderRequestDto, token);
    }

    @PutMapping("/notificar/{pedidoId}")
    @PreAuthorize("hasAuthority('EMPLEADO')")
        public Mono<RespuestaMensajeDto> notificarPedido(@RequestHeader("Authorization") String token, @PathVariable String pedidoId){
        return useCase.pedidoListo(pedidoId,token);
    }

    @PutMapping("/completar")
    @PreAuthorize("hasAuthority('EMPLEADO')")
    public Mono<String> completarPedido(@RequestHeader("Authorization") String token, @RequestBody CompletarPedidoDto completarPedidoDto){
        return useCase.completarPedido(completarPedidoDto,token);
    }

    @PutMapping("/cancelar/{pedidoId}")
    @PreAuthorize("hasAuthority('CLIENTE')")
    public Mono<String> cancelarPedido(@PathVariable Integer pedidoId) {
        return useCase.cancelarPedido(pedidoId);
    }

}
