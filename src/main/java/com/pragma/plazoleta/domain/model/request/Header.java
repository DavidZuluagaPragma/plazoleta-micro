package com.pragma.plazoleta.domain.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Header implements Serializable {

    @Value("${urlBase}")
    private String urlBase;

    @Value("${Accept}")
    private String accept;

    @Value("${Content-Type}")
    private String contentType;

}
