package com.example.gtics_ta.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ResumenDTO {

    private BigDecimal total;
    private Long cantidad;

    public ResumenDTO(Number total, Number cantidad) {
        this.total = BigDecimal.valueOf(total.doubleValue());
        this.cantidad = cantidad.longValue();
    }

}