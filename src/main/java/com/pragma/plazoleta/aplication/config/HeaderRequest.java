package com.pragma.plazoleta.aplication.config;

import com.pragma.plazoleta.domain.model.request.Header;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HeaderRequest {

    @Bean
    public Header headers(){
        return new Header();
    }

}
