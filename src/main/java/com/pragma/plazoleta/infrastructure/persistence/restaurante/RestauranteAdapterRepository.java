package com.pragma.plazoleta.infrastructure.persistence.restaurante;

import com.pragma.plazoleta.aplication.config.HeaderRequest;
import com.pragma.plazoleta.aplication.config.WebClientConfig;
import com.pragma.plazoleta.aplication.mapper.DataMapper;
import com.pragma.plazoleta.domain.model.common.Utils;
import com.pragma.plazoleta.domain.model.restaurante.Restaurante;
import com.pragma.plazoleta.domain.model.restaurante.gateway.RestauranteRepository;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Optional;


@Component
public class RestauranteAdapterRepository implements RestauranteRepository {

    @Autowired
    RestauranteDataRepository restauranteDataRepository;

    @Autowired
    private HeaderRequest header;

    @Autowired
    private WebClientConfig webClient;

    @Override
    public Mono<Boolean> esPropetario(String usuarioId) {
        try {
            return this.webClient.request()
                    .get()
                    .uri("/propetario/"+usuarioId)
                    .header(Utils.ACCEPT, header.headers().getAccept())
                    .header(Utils.CONTENT_TYPE, header.headers().getContentType())
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorResume(e -> {
                        var responseException = (WebClientResponseException) e;
                        if (e instanceof WebClientResponseException &&
                                (responseException.getStatusCode().is4xxClientError())) {
                            return Mono.error(new BusinessException(BusinessException.Type.USUARIO_NO_PROPETARIO));
                        }
                        return Mono.error(new BusinessException(BusinessException.Type.USUARIO_NO_PROPETARIO));
                    });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<Restaurante> creaRestaurante(RestauranteData restauranteData) {
        return Mono.fromCallable(() -> restauranteDataRepository.save(restauranteData))
                .map(DataMapper::convertirRestauranteDataARestaurante)
                .onErrorResume(Mono::error);
    }

    @Override
    public Mono<Optional<RestauranteData>> existeRestaurante(String restauranteId) {
        return Mono.fromCallable(() -> restauranteDataRepository.findById(Integer.valueOf(restauranteId)))
                .onErrorResume(Mono::error);
    }
}
