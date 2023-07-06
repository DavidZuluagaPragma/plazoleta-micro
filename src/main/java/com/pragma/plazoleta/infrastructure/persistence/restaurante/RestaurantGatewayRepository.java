package com.pragma.plazoleta.infrastructure.persistence.restaurante;

import com.pragma.plazoleta.aplication.config.HeaderRequest;
import com.pragma.plazoleta.aplication.config.WebClientConfig;
import com.pragma.plazoleta.aplication.mapper.DataMapper;
import com.pragma.plazoleta.domain.model.common.Utils;
import com.pragma.plazoleta.domain.model.restaurant.Restaurant;
import com.pragma.plazoleta.domain.model.restaurant.gateway.RestaurantGateway;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Optional;


@Component
public class RestaurantGatewayRepository implements RestaurantGateway {

    @Autowired
    RestaurantDataRepository restaurantDataRepository;

    @Autowired
    private HeaderRequest header;

    @Autowired
    private WebClientConfig webClient;

    @Override
    public Mono<Boolean> isOwner(String userId, String token) {
        try {
            return this.webClient.request()
                    .get()
                    .uri("/owner/" + userId)
                    .header(Utils.ACCEPT, header.headers().getAccept())
                    .header(Utils.CONTENT_TYPE, header.headers().getContentType())
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorResume(e -> {
                        var responseException = (WebClientResponseException) e;
                        if (e instanceof WebClientResponseException &&
                                (responseException.getStatusCode().is4xxClientError())) {
                            return Mono.error(new BusinessException(BusinessException.Type.USER_NOT_OWNER));
                        }
                        return Mono.error(new BusinessException(BusinessException.Type.USER_NOT_OWNER));
                    });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
    @Override
    public Mono<Restaurant> createRestaurant(RestaurantData restaurantData) {
        return Mono.fromCallable(() -> restaurantDataRepository.save(restaurantData))
                .map(DataMapper::convertRestaurantDataToRestaurant)
                .onErrorResume(Mono::error);
    }

    @Override
    public Mono<Optional<RestaurantData>> findRestaurant(String restaurantId) {
        return Mono.fromCallable(() -> restaurantDataRepository.findById(Integer.valueOf(restaurantId)))
                .onErrorResume(Mono::error);
    }

    @Override
    public Mono<Page<RestaurantData>> getRestaurants(int pageNumber, int pageSize) {
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("name").ascending());
        return Mono.fromCallable(() ->  restaurantDataRepository.findAll(pageRequest))
                .onErrorResume(Mono::error);
    }
}
