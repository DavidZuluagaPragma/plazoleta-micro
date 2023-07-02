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
        PLATO_NO_ES_DEL_PROPETARIO("El plato no es de algun restaurante del propetario"),
        ERROR_BASE_DATOS_PLATO_NO_ENCONTRADO("Error en base de datos, plato no encontrado"),
        PLATO_DE_OTRO_RESTAURANTE("El plato solicitado esta se encuentra en otro restaurante"),
        PEDIDO_ACTIVO("El cliente ya tiene un pedido activo"),
        NO_TIENE_PLATOS("No tiene platos añadidos"),
        ERROR_SOLICITUD_USUARIOS("Error al conseguir los usuarios involucrados en el pedido"),
        ERROR_BASE_DATOS_PEDIDO_NO_ENCONTRADO("Error en base de datos, pedido no encontrado"),
        ERROR_ENVIAR_MENSAJE("Error al enviar al usuario"),
        ERROR_PEDIDO_COMPLETADO("Error, sólo los pedidos que están en estado  \"Listo \" pueden pasar a estado \"entregado\""),
        ERROR_CANCELAR_PEDIDO("Lo sentimos, tu pedido ya está en preparación y no puede cancelarse"),
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
