package com.example.gtics_ta.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
public class SuperadminDTO {
    private long totalUsuarios;
    private long totalUsuariosBaneados;
    private long espaciosDisponibles;
    private long espaciosOcupados;
    private BigDecimal totalRecaudadoUltimos3Meses;
    private int reservasUltimos3Meses;
    private BigDecimal totalRecaudadoAnual;
    private int reservasAnuales;

    private List<String> mesesUltimos3Meses;
    private List<Double> recaudacionUltimos3Meses;
    private List<Long> reservasUltimos3MesesLista;

    private List<String> mesesAnuales;
    private List<Double> recaudacionAnualPorMes;
    private List<Long> reservasAnualesPorMes;

    private List<String> nombresServiciosTop;
    private List<Long> cantidadReservasTop;
    private List<String> nombresServiciosPorcentaje;
    private List<Long> cantidadServiciosPorcentaje;
    private Long cantidadTotalReservas;
    private Long cantidadReservasHoy;

}