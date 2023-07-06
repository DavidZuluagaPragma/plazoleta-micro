package com.pragma.plazoleta.domain.model.twilio.gateway;

import com.pragma.plazoleta.aplication.dto.SendMessageDto;
import com.pragma.plazoleta.aplication.dto.MessageResponseDto;
import reactor.core.publisher.Mono;

public interface TwilioGateWay {
    Mono<MessageResponseDto> sendMessage(SendMessageDto sendMessageDto, String token);
    Mono<String> validateCode(SendMessageDto sendMessageDto, String token);
}
