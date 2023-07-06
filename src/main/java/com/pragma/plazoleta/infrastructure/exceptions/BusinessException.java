package com.pragma.plazoleta.infrastructure.exceptions;

import java.util.function.Supplier;

public class BusinessException extends ApplicationException{

    private static final long serialVersionUID = 1L;

    public enum Type {
        ERROR_DATABASE_DATA_NOT_FOUND("Error in database, data not found"),
        USER_NOT_OWNER("The user is not an owner role"),
        INVALID_NAME("The name is not valid, it can contain numbers but names with only numbers are not allowed"),
        INVALID_NIT("The NIT can only be numeric"),
        INVALID_PHONE_NUMBER("The phone number can only be numeric"),
        ERROR_DATABASE_CATEGORY_NOT_FOUND("Error in database, category not found"),
        ERROR_DATABASE_RESTAURANT_NOT_FOUND("Error in database, restaurant not found"),
        EDIT_DISH_ERROR("The dish id cannot be null"),
        DISH_NOT_FROM_OWNER("The dish does not belong to any restaurant owned by the user"),
        ERROR_DATABASE_DISH_NOT_FOUND("Error in database, dish not found"),
        DISH_FROM_ANOTHER_RESTAURANT("The requested dish is from another restaurant"),
        ACTIVE_ORDER("The customer already has an active order"),
        NO_DISHES("There are no dishes added"),
        ERROR_GETTING_USERS("Error getting the users involved in the order"),
        ERROR_DATABASE_ORDER_NOT_FOUND("Error in database, order not found"),
        ERROR_SENDING_MESSAGE("Error sending message to the user"),
        ERROR_COMPLETED_ORDER("Error, only orders in \"Ready\" state can be changed to \"Delivered\" state"),
        ERROR_CANCEL_ORDER("Sorry, your order is already being prepared and cannot be cancelled"),
        ERROR_GET_TRACEABILITY("Error to get traceability");

        private final String message;

        public String getMessage() {
            return message;
        }

        public BusinessException build() {
            return new BusinessException(this);
        }

        public Supplier<Throwable> defer() {
            return () -> new BusinessException(this);
        }

        Type(String message) {
            this.message = message;
        }

    }

    private final Type type;

    public BusinessException(Type type){
        super(type.message);
        this.type = type;
    }

    public BusinessException(Type type,String menssage){
        super(menssage);
        this.type = type;
    }

    @Override
    public String getCode(){
        return type.name();
    }


}
