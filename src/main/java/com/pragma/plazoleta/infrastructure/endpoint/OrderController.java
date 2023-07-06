package com.pragma.plazoleta.infrastructure.endpoint;

import com.pragma.plazoleta.aplication.dto.*;
import com.pragma.plazoleta.domain.model.page.Page;
import com.pragma.plazoleta.domain.usecase.OrderUseCase;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;


@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    OrderUseCase useCase;

    @PostMapping("/create")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Flux<OrderResponse> createOrder(@RequestBody OrderDto orderDto, @RequestHeader("Authorization") String token) {
        return useCase.createOrder(orderDto, token);
    }
    @GetMapping("/list/{status}")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Mono<Page<OrderListDto>> listOrdersByStatus(@RequestHeader("Authorization") String token, @PathVariable String status) {
        return useCase.listOrdersByStatus(status, token);
    }
    @PutMapping("/assign")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Mono<OrderListDto> assignOrder(@RequestHeader("Authorization") String token,
                                          Principal principal,
                                          @RequestBody AssignOrderRequestDto assignOrderRequestDto) {
        return useCase.assignOrder(principal.getName(), assignOrderRequestDto, token);
    }
    @PutMapping("/notify/{orderId}")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Mono<MessageResponseDto> notifyOrder(@RequestHeader("Authorization") String token, @PathVariable String orderId){
        return useCase.orderReady(orderId,token);
    }
    @PutMapping("/complete")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Mono<String> completeOrder(@RequestHeader("Authorization") String token, @RequestBody CompleteOrderDto completeOrderDto){
        return useCase.completeOrder(completeOrderDto,token);
    }
    @PutMapping("/cancel/{orderId}")
    @PreAuthorize("hasAuthority('CLIENT')")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Mono<String> cancelOrder(@PathVariable Integer orderId, @RequestHeader("Authorization") String token) {
        return useCase.cancelOrder(orderId, token);
    }

    @GetMapping("/traceabilityByOrder")
    @PreAuthorize("hasAuthority('OWNER')")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Flux<TraceabilityByOrderResponse> getTraceabilityByOrder(@RequestHeader("Authorization") String token) {
        return useCase.getTraceabilityByOrder(token);
    }

    @GetMapping("/traceabilityByEmployed")
    @PreAuthorize("hasAuthority('OWNER')")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Flux<TraceabilityByEmployedResponse> getTraceabilityByEmployed(@RequestHeader("Authorization") String token) {
        return useCase.getTraceabilityByEmployed(token);
    }
}
