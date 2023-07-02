package com.pragma.plazoleta.domain.model.usuario.gateway;

import com.pragma.plazoleta.aplication.dto.UsuarioPedidoDto;
import com.pragma.plazoleta.aplication.dto.UsuarioPedidoRequestDto;
import com.pragma.plazoleta.domain.model.usuario.Usuario;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UsuarioGateWay {
    Mono<UsuarioPedidoDto> conseguirUsuariosDelPedido(UsuarioPedidoRequestDto usuarioPedidoRequestDto, String token);
    Mono<Usuario> findUserById(Integer userId, String token);
}
