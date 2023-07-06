package com.pragma.plazoleta.aplication.dto;

import com.pragma.plazoleta.domain.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserOrderDto implements Serializable {
    User client;
    User chef;
}
