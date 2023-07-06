package com.pragma.plazoleta.aplication.dto;

import com.pragma.plazoleta.domain.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TraceabilityByEmployedResponse {
    private User employed;
    private String efficiency;
}
