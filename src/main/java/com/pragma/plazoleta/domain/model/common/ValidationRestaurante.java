package com.pragma.plazoleta.domain.model.common;

import com.pragma.plazoleta.domain.model.restaurante.Restaurante;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

public final class ValidationRestaurante {

    public static Mono<Restaurante> validarRestauranteDto(Restaurante restaurante){
        if(restaurante.getNombre() == null || !restaurante.esNombreValido()){
            return Mono.error(new BusinessException(BusinessException.Type.NOMBRE_NO_VALIDO));
        }
        if (restaurante.getNit() == null || !esNumerico(restaurante.getNit())){
            return Mono.error(new BusinessException(BusinessException.Type.NIT_NO_VALIDO));
        }
        if (restaurante.getTelefono() == null || !esTelefonoValido(restaurante.getTelefono())){
            return Mono.error(new BusinessException(BusinessException.Type.TELEFONO_NO_VALIDO));
        }
        return Mono.just(restaurante);
    }

    public static boolean esNumerico(String valor) {
        Pattern pattern = Pattern.compile("\\d+");
        return pattern.matcher(valor).matches();
    }

    public static boolean esTelefonoValido(String valor) {
        Pattern pattern = Pattern.compile("^\\+?[0-9]{1,13}$");
        return pattern.matcher(valor).matches();
    }

}
