package com.pragma.plazoleta.aplication.dto;

import com.pragma.plazoleta.domain.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderListDto {

    private Integer id;
    private User chef;
    private User client;
    private String status;
    private List<DishResponseDto> dishList;

}
