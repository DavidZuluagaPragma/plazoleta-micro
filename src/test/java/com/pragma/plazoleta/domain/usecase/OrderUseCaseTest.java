package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.aplication.dto.*;
import com.pragma.plazoleta.domain.model.page.Page;
import com.pragma.plazoleta.domain.model.order.Order;
import com.pragma.plazoleta.domain.model.order.gateway.OrderGateway;
import com.pragma.plazoleta.domain.model.order_dish.DishOrder;
import com.pragma.plazoleta.domain.model.order_dish.gateway.DishOrderGateway;
import com.pragma.plazoleta.domain.model.dish.gateway.DishGateway;
import com.pragma.plazoleta.domain.model.restaurant.gateway.RestaurantGateway;
import com.pragma.plazoleta.domain.model.traceability.Traceability;
import com.pragma.plazoleta.domain.model.traceability.gateway.TraceabilityGateway;
import com.pragma.plazoleta.domain.model.twilio.TwilioStatus;
import com.pragma.plazoleta.domain.model.twilio.gateway.TwilioGateWay;
import com.pragma.plazoleta.domain.model.user.User;
import com.pragma.plazoleta.domain.model.user.gateway.UserGateway;
import com.pragma.plazoleta.infrastructure.exceptions.BusinessException;
import com.pragma.plazoleta.infrastructure.persistence.categoria.CategoryData;
import com.pragma.plazoleta.infrastructure.persistence.plato.DishData;
import com.pragma.plazoleta.infrastructure.persistence.restaurante.RestaurantData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderUseCaseTest {

    @InjectMocks
    OrderUseCase useCase;

    @Mock
    OrderGateway orderGateway;

    @Mock
    DishOrderGateway dishOrderGateway;

    @Mock
    DishGateway dishGateway;

    @Mock
    RestaurantGateway restaurantGateway;

    @Mock
    UserGateway userGateway;

    @Mock
    TwilioGateWay twilioGateWay;

    @Mock
    TraceabilityGateway traceabilityGateway;

    @Test
    void createOrder() {
        CategoryData category = CategoryData.builder()
                .id(1)
                .name("FAST FOOD")
                .description("FAST FOOD")
                .build();
        RestaurantData restaurant = RestaurantData.builder()
                .id(1)
                .name("PAPANATAS")
                .address("LOS PATIOS")
                .phone("+573202040834")
                .logoUrl("urlLogo")
                .nit("123456")
                .ownerId(1)
                .build();
        DishData dishData = DishData.builder()
                .price(100.0)
                .imageUrl("url")
                .name("CHOPPED")
                .description("DISH")
                .active(Boolean.TRUE)
                .category(category)
                .restaurant(restaurant)
                .build();
        OrderPlateRequestDto orderPlateRequestDto = OrderPlateRequestDto.builder()
                .dishId(1)
                .amount(1)
                .build();
        DishOrder dishOrder = DishOrder.builder()
                .orderId(1)
                .dishId(orderPlateRequestDto.getDishId())
                .amount(orderPlateRequestDto.getAmount())
                .build();
        List<OrderPlateRequestDto> orderPlateRequestDtos = new ArrayList<>();
        orderPlateRequestDtos.add(orderPlateRequestDto);
        OrderDto orderDto = OrderDto.builder()
                .restaurantId(1)
                .clientId(2)
                .dishes(orderPlateRequestDtos)
                .build();
        Order order = Order.builder()
                .status("PENDING")
                .chefId(1)
                .clientId(orderDto.getClientId())
                .date(new Date())
                .restaurantId(orderDto.getRestaurantId())
                .build();
        OrderResponse orderResponse = OrderResponse.builder()
                .status("PENDING")
                .date(order.getDate())
                .build();
        Mockito.when(orderGateway.hasActiveOrder(2)).thenReturn(Mono.just(Boolean.FALSE));
        Mockito.when(orderGateway.getMaxId()).thenReturn(Mono.just(1));
        Mockito.when(dishGateway.findDishById(orderPlateRequestDto.getDishId())).thenReturn(Mono.just(Optional.of(dishData)));
        Mockito.when(restaurantGateway.findRestaurant(dishData.getRestaurant().getId().toString())).thenReturn(Mono.just(Optional.of(restaurant)));
        Mockito.when(dishOrderGateway.createDishOrder(dishOrder)).thenReturn(Mono.just(dishOrder));
        Mockito.when(orderGateway.createOrder(order)).thenReturn(Mono.just(order));
        var result = useCase.createOrder(orderDto, "TOKEN");
        StepVerifier.create(result)
                .expectNext(orderResponse)
                .expectComplete();
    }
    @Test
    void createOrderErrorCustomerHasNoDishes() {
        OrderDto orderDto = OrderDto.builder()
                .restaurantId(1)
                .clientId(2)
                .dishes(new ArrayList<>())
                .build();
        var result = useCase.createOrder(orderDto,"TOKEN");
        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.NO_DISHES.getMessage())
                .verify();
    }

    @Test
    void createOrderErrorClientHasOrder() {
        Mockito.when(orderGateway.hasActiveOrder(2)).thenReturn(Mono.just(Boolean.TRUE));
        OrderPlateRequestDto orderPlateRequestDto = OrderPlateRequestDto.builder()
                .dishId(1)
                .amount(1)
                .build();
        List<OrderPlateRequestDto> orderPlateRequestDtos = new ArrayList<>();
        orderPlateRequestDtos.add(orderPlateRequestDto);
        OrderDto orderDto = OrderDto.builder()
                .restaurantId(1)
                .clientId(2)
                .dishes(orderPlateRequestDtos)
                .build();
        var result = useCase.createOrder(orderDto, "TOKEN");
        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.ACTIVE_ORDER.getMessage())
                .verify();
    }

    @Test
    void createOrderErrorRestaurantDoesNotExist() {
        OrderPlateRequestDto orderplateRequestDto = OrderPlateRequestDto.builder()
                .dishId(1)
                .amount(1)
                .build();
        OrderPlateRequestDto orderPlateRequestDto2 = OrderPlateRequestDto.builder()
                .dishId(1)
                .amount(1)
                .build();
        List<OrderPlateRequestDto> orderPlateRequestDtos = new ArrayList<>();
        orderPlateRequestDtos.add(orderplateRequestDto);
        orderPlateRequestDtos.add(orderPlateRequestDto2);
        OrderDto orderDto = OrderDto.builder()
                .restaurantId(1)
                .clientId(2)
                .dishes(orderPlateRequestDtos)
                .build();
        CategoryData category = CategoryData.builder()
                .id(1)
                .name("FAST FOOD")
                .description("FAST FOOD")
                .build();
        RestaurantData restaurant = RestaurantData.builder()
                .id(2)
                .name("PAPANATAS")
                .address("LOS PATIOS")
                .phone("+573202040834")
                .logoUrl("logoUrl")
                .nit("123456")
                .ownerId(1)
                .build();
        DishData dishData = DishData.builder()
                .price(100.0)
                .imageUrl("url")
                .name("PLATE")
                .description("PLATE")
                .active(Boolean.TRUE)
                .category(category)
                .restaurant(restaurant)
                .build();
        Mockito.when(orderGateway.hasActiveOrder(2)).thenReturn(Mono.just(Boolean.FALSE));
        Mockito.when(orderGateway.getMaxId()).thenReturn(Mono.just(1));
        Mockito.when(dishGateway.findDishById(orderplateRequestDto.getDishId())).thenReturn(Mono.just(Optional.of(dishData)));
        Mockito.when(restaurantGateway.findRestaurant(dishData.getRestaurant().getId().toString())).thenReturn(Mono.just(Optional.empty()));
        var result = useCase.createOrder(orderDto, "TOKEN");
        StepVerifier.create(result)
                .expectErrorMessage("last")
                .verify();
    }

    @Test
    void successfulListOrdersByState() {
        Order order = Order.builder()
                .id(1)
                .status("PENDING")
                .chefId(17)
                .clientId(1)
                .date(new Date())
                .restaurantId(1)
                .build();
        UserOrderRequestDto userOrderRequestDto = UserOrderRequestDto.builder()
                .chef(order.getChefId())
                .client(order.getClientId())
                .build();
        UserOrderDto userOrderDto = UserOrderDto.builder()
                .chef(User.builder()
                        .id(17)
                        .build())
                .client(User.builder()
                        .id(1)
                        .build())
                .build();
        DishOrder dishOrder = DishOrder.builder()
                .dishId(1)
                .amount(1)
                .build();
        DishData dishData = DishData.builder()
                .id(1)
                .price(100.0)
                .imageUrl("url")
                .name("CHOPPED")
                .description("DISH")
                .active(Boolean.TRUE)
                .category(CategoryData.builder().build())
                .restaurant(RestaurantData.builder().build())
                .build();
        DishResponseDto dishResponseDto = DishResponseDto.builder()
                .id(dishData.getId())
                .price(dishData.getPrice())
                .description(dishData.getDescription())
                .name(dishData.getName())
                .quantity(dishOrder.getAmount())
                .build();
        OrderListDto orderListDto = OrderListDto.builder()
                .id(1)
                .chef(userOrderDto.getChef())
                .client(userOrderDto.getClient())
                .status(order.getStatus())
                .dishList(List.of(dishResponseDto))
                .build();
        Page<OrderListDto> expectedPage = new Page<>(List.of(orderListDto),List.of(orderListDto).stream().count());
        Mockito.when(orderGateway.findOrderByStatus("PENDING")).thenReturn(Flux.just(order));
        Mockito.when(userGateway.getUsersFromOrder(userOrderRequestDto, "TOKEN")).thenReturn(Mono.just(userOrderDto));
        Mockito.when(dishOrderGateway.findAllOrdersById(order.getId())).thenReturn(Flux.just(dishOrder));
        Mockito.when(dishGateway.findDishById(dishOrder.getDishId())).thenReturn(Mono.just(Optional.of(dishData)));
        var result = useCase.listOrdersByStatus("PENDING", "TOKEN");
        StepVerifier.create(result)
                .expectNext(expectedPage)
                .expectComplete()
                .verify();
    }
    @Test
    void successfulAssignOrder() {
        Order order = Order.builder()
                .id(1)
                .status("PENDING")
                .chefId(17)
                .clientId(1)
                .date(new Date())
                .restaurantId(1)
                .build();
        AssignOrderRequestDto assignOrderRequestDto = AssignOrderRequestDto.builder()
                .status("IN PREPARATION")
                .orderId(order.getId())
                .build();
        UserOrderRequestDto userOrderRequestDto = UserOrderRequestDto.builder()
                .chef(order.getChefId())
                .client(order.getClientId())
                .build();
        UserOrderDto userOrderDto = UserOrderDto.builder()
                .chef(User.builder()
                        .id(17)
                        .build())
                .client(User.builder()
                        .id(1)
                        .build())
                .build();
        DishOrder dishOrder = DishOrder.builder()
                .dishId(1)
                .amount(1)
                .build();
        DishData dishData = DishData.builder()
                .id(1)
                .price(100.0)
                .imageUrl("url")
                .name("CHOPPED")
                .description("DISH")
                .active(Boolean.TRUE)
                .category(CategoryData.builder().build())
                .restaurant(RestaurantData.builder().build())
                .build();
        DishResponseDto dishResponseDto = DishResponseDto.builder()
                .id(dishData.getId())
                .price(dishData.getPrice())
                .description(dishData.getDescription())
                .name(dishData.getName())
                .quantity(dishOrder.getAmount())
                .build();
        OrderListDto orderListDto = OrderListDto.builder()
                .id(1)
                .chef(userOrderDto.getChef())
                .client(userOrderDto.getClient())
                .status(assignOrderRequestDto.getStatus())
                .dishList(List.of(dishResponseDto))
                .build();
        Mockito.when(orderGateway.findOrderById(order.getId())).thenReturn(Mono.just(order));
        Mockito.when(orderGateway.createOrder(order
                .toBuilder()
                .chefId(17)
                .status(assignOrderRequestDto.getStatus())
                .build())).thenReturn(Mono.just(order));
        Mockito.when(userGateway.getUsersFromOrder(userOrderRequestDto, "TOKEN")).thenReturn(Mono.just(userOrderDto));
        Mockito.when(dishOrderGateway.findAllOrdersById(order.getId())).thenReturn(Flux.just(dishOrder, dishOrder));
        Mockito.when(dishGateway.findDishById(dishOrder.getDishId())).thenReturn(Mono.just(Optional.of(dishData)));
        var result = useCase.assignOrder("17",assignOrderRequestDto, "TOKEN");
        StepVerifier.create(result)
                .expectNext(orderListDto)
                .expectComplete();
    }

    @Test
    void successfulOrderReady() {
        Order order = Order.builder()
                .id(1)
                .status("PENDING")
                .chefId(17)
                .clientId(1)
                .date(new Date())
                .restaurantId(1)
                .build();
        UserOrderRequestDto userOrderRequestDto = UserOrderRequestDto.builder()
                .chef(0)
                .client(order.getClientId())
                .build();
        UserOrderDto userOrderDto = UserOrderDto.builder()
                .chef(User.builder()
                        .build())
                .client(User.builder()
                        .id(1)
                        .phoneNumber("+573202040834")
                        .build())
                .build();
        SendMessageDto sendMessageDto = SendMessageDto.builder()
                .destinationNumber(userOrderDto.getClient().getPhoneNumber())
                .user("javatechie")
                .build();
        MessageResponseDto messageResponseDto = MessageResponseDto.builder()
                .message("HELLO")
                .status(TwilioStatus.DELIVERED)
                .build();
        Mockito.when(orderGateway.findOrderById(order.getId())).thenReturn(Mono.just(order));
        Mockito.when(orderGateway.createOrder(order.toBuilder()
                .status("ORDER READY")
                .build())).thenReturn(Mono.just(order));
        Mockito.when(userGateway.getUsersFromOrder(userOrderRequestDto,"TOKEN")).thenReturn(Mono.just(userOrderDto));
        Mockito.when(twilioGateWay.sendMessage(sendMessageDto, "TOKEN")).thenReturn(Mono.just(messageResponseDto));
        var result = useCase.orderReady("1", "TOKEN");
        StepVerifier.create(result)
                .expectNext(messageResponseDto)
                .expectComplete();
    }
    @Test
    void successfulOrderComplete() {
        CompleteOrderDto completeOrderDto = CompleteOrderDto.builder()
                .code("123456")
                .orderId(1)
                .build();
        SendMessageDto sendMessageDto = SendMessageDto.builder()
                .password("123456")
                .user("user")
                .build();
        Order order = Order.builder()
                .id(1)
                .status("READY")
                .chefId(17)
                .clientId(1)
                .date(new Date())
                .restaurantId(1)
                .build();
        String orderComplete = "ORDER COMPLETED!!";
        Mockito.when(twilioGateWay.validateCode(sendMessageDto, "TOKEN")).thenReturn(Mono.just("MESSAGE SENT"));
        Mockito.when(orderGateway.findOrderById(order.getId())).thenReturn(Mono.just(order));
        Mockito.when(orderGateway.createOrder(order.toBuilder()
                .status("DELIVERED")
                .build())).thenReturn(Mono.just(order));
        var result = useCase.completeOrder(completeOrderDto, "TOKEN");

        StepVerifier.create(result)
                .expectNext(orderComplete)
                .expectComplete()
                .verify();
    }

    @Test
    void completeOrderErrorMessageNotAccepted() {
        CompleteOrderDto completeOrderDto = CompleteOrderDto.builder()
                .code("123456")
                .orderId(1)
                .build();
        SendMessageDto sendMessageDto = SendMessageDto.builder()
                .password("123456")
                .user("user")
                .build();
        Mockito.when(twilioGateWay.validateCode(sendMessageDto, "TOKEN")).thenReturn(Mono.just("Invalid OTP, try again!"));
        var result = useCase.completeOrder(completeOrderDto, "TOKEN");
        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.ERROR_COMPLETED_ORDER.getMessage())
                .verify();
    }
    @Test
    void completeOrderErrorOrderNotReady() {
        CompleteOrderDto completeOrderDto = CompleteOrderDto.builder()
                .code("123456")
                .orderId(1)
                .build();
        SendMessageDto sendMessageDto = SendMessageDto.builder()
                .password("123456")
                .user("user")
                .build();
        Order order = Order.builder()
                .id(1)
                .status("PENDING")
                .chefId(17)
                .clientId(1)
                .date(new Date())
                .restaurantId(1)
                .build();
        Mockito.when(twilioGateWay.validateCode(sendMessageDto, "TOKEN")).thenReturn(Mono.just("Invalid OTP, try again!"));
        Mockito.when(orderGateway.findOrderById(order.getId())).thenReturn(Mono.just(order));
        var result = useCase.completeOrder(completeOrderDto, "TOKEN");
        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.ERROR_COMPLETED_ORDER.getMessage())
                .verify();
    }
    @Test
    void successfulCancelOrder() {
        Order order = Order.builder()
                .id(1)
                .status("PENDING")
                .chefId(17)
                .clientId(1)
                .date(new Date())
                .restaurantId(1)
                .build();
        Order cancelOrder = Order.builder()
                .id(1)
                .status("CANCELLED")
                .chefId(17)
                .clientId(1)
                .date(new Date())
                .restaurantId(1)
                .build();
        Mockito.when(orderGateway.findOrderById(order.getId())).thenReturn(Mono.just(order));
        Mockito.when(orderGateway.createOrder(cancelOrder)).thenReturn(Mono.just(cancelOrder));
        var result = useCase.cancelOrder(order.getId(), "TOKEN");
        StepVerifier.create(result)
                .expectNext("ORDER CANCELLED SUCCESSFULLY")
                .expectComplete();
    }

    @Test
    void cancelOrderError() {
        Order order = Order.builder()
                .id(1)
                .status("READY")
                .chefId(17)
                .clientId(1)
                .date(new Date())
                .restaurantId(1)
                .build();
        Mockito.when(orderGateway.findOrderById(order.getId())).thenReturn(Mono.just(order));
        var result = useCase.cancelOrder(order.getId(),"TOKEN");
        StepVerifier.create(result)
                .expectErrorMessage(BusinessException.Type.ERROR_CANCEL_ORDER.getMessage())
                .verify();
    }
    @Test
    void getTraceabilityByOrderSuccessful() {
        Traceability traceability = Traceability.builder()
                .orderId(1)
                .clientId(1)
                .date(new Date())
                .build();
        Order order = Order.builder()
                .id(1)
                .status("READY")
                .date(new Date())
                .build();
        TraceabilityByOrderResponse traceabilityByOrderResponse = TraceabilityByOrderResponse.builder()
                .orderId(order.getId())
                .startDate(order.getDate())
                .endDate(traceability.getDate())
                .efficiency(order.calculateOrderTime(traceability.getDate()))
                .build();
        Mockito.when(traceabilityGateway.getAllCompletedTraceability("TOKEN")).thenReturn(Flux.just(traceability));
        Mockito.when(orderGateway.findOrderById(traceability.getOrderId())).thenReturn(Mono.just(order));
        var result = useCase.getTraceabilityByOrder("TOKEN");
        StepVerifier.create(result)
                .expectNext(traceabilityByOrderResponse)
                .expectComplete()
                .verify();
    }
    @Test
    void getTraceabilityByEmployedSuccessful() {
        Traceability traceability = Traceability.builder()
                .orderId(1)
                .clientId(1)
                .date(new Date())
                .employedId(2)
                .build();
        Order order = Order.builder()
                .id(1)
                .status("READY")
                .date(new Date())
                .build();
        User user = User.builder()
                .id(2)
                .build();
        TraceabilityByEmployedResponse traceabilityByEmployedResponse = TraceabilityByEmployedResponse.builder()
                .employed(user)
                .efficiency("0 minutes")
                .build();
        Mockito.when(traceabilityGateway.getAllCompletedTraceability("TOKEN")).thenReturn(Flux.just(traceability));
        Mockito.when(orderGateway.findOrderById(traceability.getOrderId())).thenReturn(Mono.just(order));
        Mockito.when(userGateway.findUserById(traceability.getEmployedId(),"TOKEN")).thenReturn(Mono.just(user));
        var result = useCase.getTraceabilityByEmployed("TOKEN");
        StepVerifier.create(result)
                .expectNext(traceabilityByEmployedResponse)
                .expectComplete()
                .verify();
    }

}