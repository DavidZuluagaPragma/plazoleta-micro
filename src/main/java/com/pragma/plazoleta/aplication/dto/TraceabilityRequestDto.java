package com.pragma.trazabilidad.aplication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TraceabilityRequestDto {
    private Integer orderId;
    private Integer clientId;
    private String clientEmail;
    private String newStatus;
    private Integer employedId;
    private String employedEmail;
}
