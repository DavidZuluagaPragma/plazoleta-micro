package com.pragma.plazoleta.aplication.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderResponse {
    private String status;
    private Date date;
}
