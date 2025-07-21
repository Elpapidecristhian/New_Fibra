package com.example.gtics_ta.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class MensajeProcesadoDTO {

    // 🔸 Para deserialización del JSON desde OpenAI
    private String espacio;            // ex: "piscina Diego Ferre"
    private String fecha;              // ex: "2025-07-15"
    private List<String> intenciones; // ex: ["costo_reserva", "aforo_espacio"]

    // 🔸 Uso interno para respuestas combinadas
    private String espacioDetectado;
    private LocalDate fechaDetectada;
    private List<String> intencionesDetectadas;

    private List<String> horariosDisponibles;
    private BigDecimal costo;
    private Integer aforo;
    private List<String> mediosPago;
    private String detalleEspecifico;
    private String detalle; // 👈 Agrega esto

    private String respuestaGenerada;
}