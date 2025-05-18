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
    private Integer id;
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
    @JoinColumn(name = "id_horario_reservado")
    private HorarioReservado horarioReservado;
    @Column(name = "registro_timestamp")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_reserva")
    private LocalDate fechaReserva;

    @Column(name = "estado", nullable = false)
    private String estado = "Disponible";

}