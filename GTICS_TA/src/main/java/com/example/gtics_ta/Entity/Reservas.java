package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "reservas")
public class Reservas {
    @Id
    @Column(name = "id_reservas")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "id_espacio")
    private EspaciosDeportivos espacioDeportivo;
    @ManyToOne
    @JoinColumn(name = "id_pagos")
    private Pagos pago;
    @ManyToOne
    @JoinColumn(name = "id_horarios")
    private Horarios horario;
    @Column(name = "registro_timestamp")
    private Timestamp fechaRegistro;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_reserva")
    private LocalDate fechaReserva;

    // Nuevos campos para gestión de reservas
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_reserva")
    private EstadoReserva estadoReserva = EstadoReserva.ACTIVA;

    @Column(name = "motivo_cancelacion")
    private String motivoCancelacion;

    @Column(name = "fecha_cancelacion")
    private Timestamp fechaCancelacion;

    @ManyToOne
    @JoinColumn(name = "cancelado_por")
    private Usuario canceladoPor;

    @ManyToOne
    @JoinColumn(name = "id_mantenimiento")
    private Mantenimiento mantenimiento;

    @Column(name = "reembolso_procesado")
    private Boolean reembolsoProcesado = false;

    // Enum para estado de reserva
    public enum EstadoReserva {
        ACTIVA, CANCELADA_USUARIO, CANCELADA_MANTENIMIENTO, CANCELADA_ADMIN, COMPLETADA
    }

}
