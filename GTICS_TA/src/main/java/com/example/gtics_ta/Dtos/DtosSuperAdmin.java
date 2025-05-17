package com.example.gtics_ta.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

public class DtosSuperAdmin {

    @Data
    @AllArgsConstructor
    public static class ServicioTop {
        private String nombre;
        private Long cantidad;
    }

    @Data
    @AllArgsConstructor
    public static class PorcentajeTipo {
        private String tipo;
        private Long cantidad;
    }

    @Data
    @AllArgsConstructor
    public static class ReporteMensual {
        private String mes;
        private Double ingresoTotal;
        private Long cantidadReservas;
    }
}
