package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.dto.*;
import com.pragma.plazoleta.domain.model.common.Utils;
import com.pragma.plazoleta.domain.model.page.Page;
import com.pragma.plazoleta.domain.model.order.Order;
import com.pragma.plazoleta.domain.model.order.gateway.OrderGateway;
import com.pragma.plazoleta.domain.model.order_dish.DishOrder;
import com.pragma.plazoleta.domain.model.order_dish.gateway.DishOrderGateway;
import com.pragma.plazoleta.domain.model.dish.gateway.DishGateway;
import com.pragma.plazoleta.domain.model.restaurant.gateway.RestaurantGateway;
import com.pragma.plazoleta.domain.model.traceability.gateway.TraceabilityGateway;
import com.pragma.plazoleta.domain.model.twilio.gateway.TwilioGateWay;
import com.pragma.plazoleta.domain.model.user.User;
import com.pragma.plazoleta.domain.model.user.gateway.UserGateway;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@RequiredArgsConstructor
@Component
public class OrderUseCase {

    @Autowired
    OrderGateway orderGateway;

    @Autowired
    DishOrderGateway dishOrderGateway;

    @Autowired
    DishGateway dishGateway;

    @Autowired
    RestaurantGateway restaurantGateway;

    @Autowired
    UserGateway userGateway;

    @Autowired
    TwilioGateWay twilioGateWay;

    @Autowired
    TraceabilityGateway traceabilityGateway;


    /**
     * Método para crear pedido.
     *
     * @param orderDto objeto con los datos necesarios para crear el pedido
     * @param token    el token de autenticación
     * @return Una lista de OrderResponse el cual se compone de un estado y una fecha
     */
    public Flux<OrderResponse> createOrder(OrderDto orderDto, String token) {
        // Comprueba si la lista de platos está vacía
        if (orderDto.getDishes().isEmpty()) {
            return Flux.error(new BusinessException(BusinessException.Type.NO_DISHES));
        }
        // Comprueba si el cliente ya tiene un pedido activo
        return orderGateway.hasActiveOrder(orderDto.getClientId())
                .flatMapMany(hasActiveOrder -> {
                    if (Boolean.TRUE.equals(hasActiveOrder)) {
                        return Flux.error(new BusinessException(BusinessException.Type.ACTIVE_ORDER));
                    }
                    // Obtiene el ID máximo de los pedidos existentes
                    return orderGateway.getMaxId()
                            .flatMapMany(orderId -> Flux.fromIterable(orderDto.getDishes())
                                    .flatMap(dishes ->
                                            // Busca el plato por su ID
                                            dishGateway.findDishById(dishes.getDishId())
                                                    .flatMap(dishData -> dishData.map(data ->
                                                            // Busca el restaurante por su ID
                                                            restaurantGateway.findRestaurant(data.getRestaurant().getId().toString())
                                                                    .flatMap(restaurantData -> {
                                                                        // Comprueba si el restaurante existe
                                                                        if (restaurantData.isEmpty()) {
                                                                            return Mono.error(new BusinessException(BusinessException.Type.ERROR_DATABASE_RESTAURANT_NOT_FOUND));
                                                                        }
                                                                        // Comprueba si el plato pertenece al restaurante del pedido
                                                                        if (!Objects.equals(restaurantData.get().getId(), orderDto.getRestaurantId())) {
                                                                            return Mono.error(new BusinessException(BusinessException.Type.DISH_FROM_ANOTHER_RESTAURANT));
                                                                        }
                                                                        // Crea un nuevo pedido de plato
                                                                        return dishOrderGateway.createDishOrder(DishOrder.builder()
                                                                                .orderId(orderId)
                                                                                .dishId(dishes.getDishId())
                                                                                .amount(dishes.getAmount())
                                                                                .build());
                                                                    })).orElseGet(() -> Mono.error(new BusinessException(BusinessException.Type.ERROR_DATABASE_DISH_NOT_FOUND)))
                                                    )
                                    )
                                    // Crea un nuevo pedido
                                    .then(orderGateway.createOrder(Order.builder()
                                            .status(Utils.ORDER_PENDING)
                                            .chefId(0)
                                            .clientId(orderDto.getClientId())
                                            .date(new Date())
                                            .restaurantId(orderDto.getRestaurantId())
                                            .build()))
                                    .flatMap(order ->
                                            // Crea una nueva traza para el pedido
                                            traceabilityGateway.createTraceabilityForOrder(TraceabilityRequestDto.builder()
                                                            .orderId(order.getId())
                                                            .clientId(order.getId())
                                                            .newStatus(Utils.ORDER_PENDING)
                                                            .build(), token)
                                                    .thenReturn(OrderResponse.builder()
                                                            .status(Utils.ORDER_PENDING)
                                                            .date(order.getDate())
                                                            .build())));
                });
    }


    /**
     * Método para listar órdenes por estado.
     *
     * @param status el estado de las órdenes a listar
     * @param token  el token de autenticación
     * @return una página de objetos OrderListDto que contienen información de las órdenes
     */
    public Mono<Page<OrderListDto>> listOrdersByStatus(String status, String token) {
        // Buscar órdenes por estado
        return orderGateway.findOrderByStatus(status)
                .flatMap(order -> {
                    List<DishResponseDto> dishList = new ArrayList<>();
                    // Obtener información de usuarios relacionados a la orden
                    return userGateway.getUsersFromOrder(UserOrderRequestDto.builder()
                                    .chef(order.getChefId())
                                    .client(order.getClientId())
                                    .build(), token)
                            .flatMap(userOrderDto ->
                                    // Obtener información de platos relacionados a la orden
                                    dishOrderGateway.findAllOrdersById(order.getId())
                                            .flatMap(orderDish -> dishGateway.findDishById(orderDish.getDishId())
                                                    .map(dishData -> DishResponseDto.builder()
                                                            .id(dishData.get().getId())
                                                            .description(dishData.get().getDescription())
                                                            .price(dishData.get().getPrice())
                                                            .name(dishData.get().getName())
                                                            .quantity(orderDish.getAmount())
                                                            .build())
                                                    .doOnNext(dishList::add))
                                            .then(Mono.just(OrderListDto.builder()
                                                    .id(order.getId())
                                                    .client(userOrderDto.getClient())
                                                    .chef(userOrderDto.getChef())
                                                    .status(status)
                                                    .dishList(dishList)
                                                    .build())));
                })
                .collectList()
                .map(orderListDtos -> new Page<>(orderListDtos, orderListDtos.stream().count()));
    }


    /**
     * Método para asignar una orden.
     *
     * @param assignOrderRequestDto objeto con los datos necesarios para asignar el pedido
     * @param userId                el usuario ID
     * @param token                 el token de autenticación
     * @return un objeto de tipo OrderListDto
     */
    public Mono<OrderListDto> assignOrder(String userId, AssignOrderRequestDto assignOrderRequestDto, String token) {
        // Buscar el pedido por su ID
        return orderGateway.findOrderById(assignOrderRequestDto.getOrderId())
                .flatMap(order ->
                        // Crear un nuevo pedido con el ID del chef y el estado del pedido
                        orderGateway.createOrder(order.toBuilder()
                                        .chefId(Integer.parseInt(userId))
                                        .status(Utils.ORDER_PREPARATION)
                                        .build())
                                .flatMap(modifiedOrder ->
                                        // Obtener información de los usuarios relacionados con el pedido (chef y cliente)
                                        userGateway.getUsersFromOrder(UserOrderRequestDto.builder()
                                                        .chef(Integer.parseInt(userId))
                                                        .client(order.getClientId())
                                                        .build(), token)
                                                .flatMap(userOrderDto ->
                                                        // Buscar todos los pedidos por su ID
                                                        dishOrderGateway.findAllOrdersById(order.getId())
                                                                .flatMap(dishOrder ->
                                                                        // Buscar el plato por su ID
                                                                        dishGateway.findDishById(dishOrder.getDishId())
                                                                                .map(dishData -> DishResponseDto.builder()
                                                                                        .id(dishData.get().getId())
                                                                                        .description(dishData.get().getDescription())
                                                                                        .price(dishData.get().getPrice())
                                                                                        .name(dishData.get().getName())
                                                                                        .quantity(dishOrder.getAmount())
                                                                                        .build()))
                                                                .collectList() // Recopilar todos los elementos en una lista
                                                                .flatMap(dishList -> {
                                                                    dishList.remove(0); // Eliminar el primer elemento de la lista
                                                                    // Crear trazabilidad para el pedido
                                                                    return traceabilityGateway.createTraceabilityForOrder(TraceabilityRequestDto.builder()
                                                                                    .orderId(order.getId())
                                                                                    .employedId(userOrderDto.getChef().getId())
                                                                                    .employedEmail(userOrderDto.getChef().getEmail())
                                                                                    .clientEmail(userOrderDto.getClient().getEmail())
                                                                                    .newStatus(Utils.ORDER_PREPARATION)
                                                                                    .build(), token)
                                                                            .thenReturn(OrderListDto.builder() // Devolver el objeto OrderListDto
                                                                                    .id(order.getId())
                                                                                    .client(userOrderDto.getClient())
                                                                                    .chef(userOrderDto.getChef())
                                                                                    .status(assignOrderRequestDto.getStatus())
                                                                                    .dishList(dishList)
                                                                                    .build());
                                                                }))));
    }

    /**
     * Método para cambiar el estado una orden a listo.
     *
     * @param orderId el id de la orden a buscar para modificar
     * @param token   el token de autenticación
     * @return un objeto de tipo MessageResponseDto
     */
    public Mono<MessageResponseDto> orderReady(String orderId, String token) {
        return orderGateway.findOrderById(Integer.parseInt(orderId)) //Busca la orden por id
                .flatMap(order -> orderGateway.createOrder(order.toBuilder()
                        .status(Utils.ORDER_READY)
                        .build()))
                .flatMap(order -> userGateway.getUsersFromOrder(UserOrderRequestDto.builder() //Busca los usuarios involucrados en la orden
                        .chef(0)
                        .client(order.getClientId())
                        .build(), token))
                .flatMap(userOrderDto -> traceabilityGateway.createTraceabilityForOrder(TraceabilityRequestDto.builder() //Crea una trazabilidad del proceso
                                .orderId(Integer.parseInt(orderId))
                                .newStatus(Utils.ORDER_READY)
                                .build(), token)
                        .flatMap(traceability -> twilioGateWay.sendMessage(SendMessageDto.builder() //Envia un mensaje al cliente para notifica que el pedido esta listo
                                .destinationNumber(userOrderDto.getClient().getPhoneNumber())
                                .user(Utils.USER)
                                .build(), token)));
    }

    /**
     * Método para completar una orden.
     *
     * @param completeOrderDto objeto con los datos necesarios para completar el pedido
     * @param token            el token de autenticación
     * @return un mensaje  indicando que el pedido ha sido completado
     */
    public Mono<String> completeOrder(CompleteOrderDto completeOrderDto, String token) {
        return twilioGateWay.validateCode(SendMessageDto.builder() //Validar el codigo recibido por el usuario
                        .password(completeOrderDto.getCode())
                        .user(Utils.USER)
                        .build(), token)
                .flatMap(messageResponse -> {
                    if (messageResponse.equals(Utils.INVALID_OTP)) {
                        return Mono.error(new BusinessException(BusinessException.Type.ERROR_COMPLETED_ORDER));
                    }
                    return Mono.just(messageResponse);
                })
                .flatMap(response -> orderGateway.findOrderById(completeOrderDto.getOrderId()) //Encontrar la orden por id
                        .flatMap(order -> {
                            if (!order.getStatus().equals(Utils.ORDER_READY)) {
                                return Mono.error(new BusinessException(BusinessException.Type.ERROR_COMPLETED_ORDER));
                            }
                            return orderGateway.createOrder(order.toBuilder()
                                    .status(Utils.ORDER_DELIVERED)
                                    .build());
                        }))
                .flatMap(order -> traceabilityGateway.createTraceabilityForOrder(TraceabilityRequestDto.builder() //Crear una trazabilidad
                        .orderId(order.getId())
                        .newStatus(Utils.ORDER_DELIVERED)
                        .build(), token))
                .thenReturn(Utils.ORDER_COMPLETED)
                .onErrorResume(Mono::error);
    }

    /**
     * Método para cancelar una orden.
     *
     * @param orderId id del pedido a cancelar
     * @param token   el token de autenticación
     * @return un mensaje  indicando que el pedido ha sido cancelado
     */
    public Mono<String> cancelOrder(Integer orderId, String token) {
        return orderGateway.findOrderById(orderId)
                .flatMap(order -> {
                    if (!order.getStatus().equals(Utils.ORDER_PENDING)) {
                        return Mono.error(new BusinessException(BusinessException.Type.ERROR_CANCEL_ORDER));
                    }
                    return orderGateway.createOrder(order.toBuilder()
                            .status(Utils.ORDER_CANCELLED)
                            .build());
                })
                .flatMap(order -> traceabilityGateway.createTraceabilityForOrder(TraceabilityRequestDto.builder()
                        .orderId(order.getId())
                        .newStatus(Utils.ORDER_CANCELLED)
                        .build(), token))
                .thenReturn(Utils.ORDER_CANCELLED_SUCCESS);
    }

    /**
     * Método para conseguir la eficiencia por pedido.
     *
     * @param token el token de autenticación
     * @return una lista de tipo TraceabilityByOrderResponse
     */
    public Flux<TraceabilityByOrderResponse> getTraceabilityByOrder(String token) {
        return traceabilityGateway.getAllCompletedTraceability(token)
                .flatMap(traceability -> orderGateway.findOrderById(traceability.getOrderId())
                        .map(order -> TraceabilityByOrderResponse.builder()
                                .orderId(order.getId())
                                .startDate(order.getDate())
                                .endDate(traceability.getDate())
                                .efficiency(order.calculateOrderTime(traceability.getDate()))
                                .build()));
    }


    /**
     * Método para conseguir la eficiencia por empleado.
     *
     * @param token el token de autenticación
     * @return una lista de tipo TraceabilityByEmployedResponse
     */
    public Flux<TraceabilityByEmployedResponse> getTraceabilityByEmployed(String token) {
        return traceabilityGateway.getAllCompletedTraceability(token)
                .flatMap(traceability -> orderGateway.findOrderById(traceability.getOrderId())
                        .flatMap(order -> userGateway.findUserById(traceability.getEmployedId(), token)
                                .map(user -> {
                                    long timeHalf = traceability.getDate().getTime() - order.getDate().getTime();
                                    return new TraceabilityByEmployedResponse(user, Long.toString(timeHalf));
                                })
                        )
                )
                .groupBy(TraceabilityByEmployedResponse::getEmployed) // Group by employee
                .flatMap(groupedFlux -> groupedFlux.collectList()
                        .map(traceabilityByEmployedResponses -> {
                            User employee = traceabilityByEmployedResponses.get(0).getEmployed(); // Get the employee of the group
                            String efficiencyAverage = Utils.calculateAverageEfficiency(traceabilityByEmployedResponses); // Calculate average efficiency
                            return new TraceabilityByEmployedResponse(employee, efficiencyAverage);
                        })
                );
    }

}
