package com.example.gtics_ta.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class AdminDTO {
    private long totalUsuarios;
    private long totalUsuariosBaneados;
    private long cantidadTotalReservas;
    private long cantidadReservasHoy;
    private long espaciosDisponibles;

    private List<String> nombresServiciosTop;
    private List<Long> cantidadReservasTop;

    private List<String> nombresServiciosPorcentaje;
    private List<Long> cantidadServiciosPorcentaje;
    private List<String> horasReservas; // ej: ["08:00", "09:00", ...]
    private List<Long> cantidadReservasPorHora;

    private List<String> nombresUsuariosTop;
    private List<Long> cantidadReservasUsuariosTop;

}
