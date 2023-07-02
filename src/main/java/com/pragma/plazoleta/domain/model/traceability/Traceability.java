package com.pragma.plazoleta.domain.model.traceability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Traceability {
    private Integer orderId;
    private Integer clientId;
    private Date date;
    private String oldStatus;
    private String newStatus;
    private Integer employedId;
}
