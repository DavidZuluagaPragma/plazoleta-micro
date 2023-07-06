package com.pragma.plazoleta.domain.model.restaurant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Restaurant {
    private Integer id;
    private String name;
    private String address;
    private String phone;
    private String logoUrl;
    private String nit;
    private Integer ownerId;
    public boolean isNameValid() {
        return !name.matches("\\d+");
    }
}
