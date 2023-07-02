package com.pragma.plazoleta.infrastructure.web_service;

import com.pragma.plazoleta.aplication.config.HeaderRequest;
import com.pragma.plazoleta.aplication.config.WebClientConfig;
import com.pragma.plazoleta.aplication.dto.UsuarioPedidoDto;
import com.pragma.plazoleta.aplication.dto.UsuarioPedidoRequestDto;
import com.pragma.plazoleta.domain.model.common.Utils;
import com.pragma.plazoleta.domain.model.usuario.Usuario;
import com.pragma.plazoleta.domain.model.usuario.gateway.UsuarioGateWay;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class UsuarioService implements UsuarioGateWay {

    @Autowired
    private HeaderRequest header;

    @Autowired
    private WebClientConfig webClient;

    @Override
    public Mono<UsuarioPedidoDto> conseguirUsuariosDelPedido(UsuarioPedidoRequestDto usuarioPedidoRequestDto, String token) {
        try {
            return this.webClient.request()
                    .post()
                    .uri("/pedidos" )
                    .header(Utils.ACCEPT, header.headers().getAccept())
                    .header(Utils.CONTENT_TYPE, header.headers().getContentType())
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .body(Mono.just(usuarioPedidoRequestDto),UsuarioPedidoRequestDto.class)
                    .retrieve()
                    .bodyToMono(UsuarioPedidoDto.class)
                    .onErrorResume(e -> {
                        var responseException = (WebClientResponseException) e;
                        if (e instanceof WebClientResponseException &&
                                (responseException.getStatusCode().is4xxClientError())) {
                            return Mono.error(new BusinessException(BusinessException.Type.ERROR_SOLICITUD_USUARIOS));
                        }
                        return Mono.error(new BusinessException(BusinessException.Type.ERROR_SOLICITUD_USUARIOS));
                    });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<Usuario> findUserById(Integer userId, String token) {
        try {
            return this.webClient.request()
                    .get()
                    .uri("/usuario/"+userId )
                    .header(Utils.ACCEPT, header.headers().getAccept())
                    .header(Utils.CONTENT_TYPE, header.headers().getContentType())
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(Usuario.class)
                    .onErrorResume(e -> {
                        var responseException = (WebClientResponseException) e;
                        if (e instanceof WebClientResponseException &&
                                (responseException.getStatusCode().is4xxClientError())) {
                            return Mono.error(new BusinessException(BusinessException.Type.ERROR_SOLICITUD_USUARIOS));
                        }
                        return Mono.error(new BusinessException(BusinessException.Type.ERROR_SOLICITUD_USUARIOS));
                    });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
