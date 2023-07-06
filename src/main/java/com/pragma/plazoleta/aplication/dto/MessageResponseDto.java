package com.pragma.plazoleta.aplication.dto;

import com.pragma.plazoleta.domain.model.twilio.TwilioStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MessageResponseDto {
    private TwilioStatus status;
    private String message;
}
