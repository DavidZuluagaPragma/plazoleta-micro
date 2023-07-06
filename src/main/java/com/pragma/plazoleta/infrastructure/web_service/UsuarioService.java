package com.pragma.plazoleta.infrastructure.web_service;

import com.pragma.plazoleta.aplication.config.HeaderRequest;
import com.pragma.plazoleta.aplication.config.WebClientConfig;
import com.pragma.plazoleta.aplication.dto.UserOrderDto;
import com.pragma.plazoleta.aplication.dto.UserOrderRequestDto;
import com.pragma.plazoleta.domain.model.common.Utils;
import com.pragma.plazoleta.domain.model.user.User;
import com.pragma.plazoleta.domain.model.user.gateway.UserGateway;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class UsuarioService implements UserGateway {

    @Autowired
    private HeaderRequest header;

    @Autowired
    private WebClientConfig webClient;

    @Override
    public Mono<UserOrderDto> getUsersFromOrder(UserOrderRequestDto userOrderRequestDto, String token) {
        try {
            return this.webClient.request()
                    .post()
                    .uri("/orders")
                    .header(Utils.ACCEPT, header.headers().getAccept())
                    .header(Utils.CONTENT_TYPE, header.headers().getContentType())
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .body(Mono.just(userOrderRequestDto), UserOrderRequestDto.class)
                    .retrieve()
                    .bodyToMono(UserOrderDto.class)
                    .onErrorResume(e -> {
                        var responseException = (WebClientResponseException) e;
                        if (e instanceof WebClientResponseException &&
                                (responseException.getStatusCode().is4xxClientError())) {
                            return Mono.error(new BusinessException(BusinessException.Type.ERROR_GETTING_USERS));
                        }
                        return Mono.error(new BusinessException(BusinessException.Type.ERROR_GETTING_USERS));
                    });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<User> findUserById(Integer userId, String token) {
        try {
            return this.webClient.request()
                    .get()
                    .uri("/user/" + userId)
                    .header(Utils.ACCEPT, header.headers().getAccept())
                    .header(Utils.CONTENT_TYPE, header.headers().getContentType())
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(User.class)
                    .onErrorResume(e -> {
                        var responseException = (WebClientResponseException) e;
                        if (e instanceof WebClientResponseException &&
                                (responseException.getStatusCode().is4xxClientError())) {
                            return Mono.error(new BusinessException(BusinessException.Type.ERROR_GETTING_USERS));
                        }
                        return Mono.error(new BusinessException(BusinessException.Type.ERROR_GETTING_USERS));
                    });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
