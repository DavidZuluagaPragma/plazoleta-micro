package com.pragma.plazoleta.domain.model.common;

import com.pragma.plazoleta.domain.model.restaurant.Restaurant;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

public final class ValidationRestaurant {

    public static Mono<Restaurant> validateRestaurantDto(Restaurant restaurant){
        if(restaurant.getName() == null || !restaurant.isNameValid()){
            return Mono.error(new BusinessException(BusinessException.Type.INVALID_NAME));
        }
        if (restaurant.getNit() == null || !isNumeric(restaurant.getNit())){
            return Mono.error(new BusinessException(BusinessException.Type.INVALID_NIT));
        }
        if (restaurant.getPhone() == null || !isValidPhoneNumber(restaurant.getPhone())){
            return Mono.error(new BusinessException(BusinessException.Type.INVALID_PHONE_NUMBER));
        }
        return Mono.just(restaurant);
    }
    public static boolean isNumeric(String value) {
        Pattern pattern = Pattern.compile("\\d+");
        return pattern.matcher(value).matches();
    }
    public static boolean isValidPhoneNumber(String value) {
        Pattern pattern = Pattern.compile("^\\+?[0-9]{1,13}$");
        return pattern.matcher(value).matches();
    }

}
