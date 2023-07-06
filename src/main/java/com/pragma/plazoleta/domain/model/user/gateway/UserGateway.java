package com.pragma.plazoleta.domain.model.user.gateway;

import com.pragma.plazoleta.aplication.dto.UserOrderDto;
import com.pragma.plazoleta.aplication.dto.UserOrderRequestDto;
import com.pragma.plazoleta.domain.model.user.User;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserGateway {
    Mono<UserOrderDto> getUsersFromOrder(UserOrderRequestDto userOrderRequestDto, String token);
    Mono<User> findUserById(Integer userId, String token);
}
