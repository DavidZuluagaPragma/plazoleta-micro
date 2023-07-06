package com.pragma.plazoleta.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class orderListResponseDto {
    private List<OrderListDto> orderList;
}
