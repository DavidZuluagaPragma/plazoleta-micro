package com.pragma.plazoleta.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User implements Serializable {
    private Integer id;
    private String name;
    private String lastName;
    private String numberDocument;
    private String phoneNumber;
    private String dateOfBirth;
    private String email;
}
