package com.pragma.plazoleta.infrastructure.exceptions;

import java.util.function.Supplier;

public class BusinessException extends ApplicationException{

    private static final long serialVersionUID = 1L;

    public enum Type {
        ERROR_BASE_DATOS_DATO_NO_ENCONTRADO("Error en base de datos, dato no encontrado"),
        USUARIO_NO_PROPETARIO("El usuario no es rol propetario"),
        NOMBRE_NO_VALIDO("El nombre no es valido, puede contener numeros pero no se permiten nombres con solo numeros"),
        NIT_NO_VALIDO("El nit solo puede ser numerico"),
        TELEFONO_NO_VALIDO("El telefono solo puede ser numerico"),
        ERROR_BASE_DATOS_CATEGORIA_NO_ENCONTRADA("Error en base de datos, categoria no encontrada"),
        ERROR_BASE_DATOS_RESTAURANTE_NO_ENCONTRADA("Error en base de datos, restaurante no encontrado"),
        EDITAR_PLATO_ERROR("El id del plano no puede ser nulo"),
        ERROR_BASE_DATOS_PLATO_NO_ENCONTRADO("Error en base de datos, plato no encontrado");

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
