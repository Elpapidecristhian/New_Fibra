package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "reservas")
public class Reservas {
    @Id
    @Column(name = "idReservas")
    private int id;
    @ManyToOne
    private Usuario usuario;
    @ManyToOne
    private EspaciosDeportivos espacioDeportivo;
    @ManyToOne
    private Pagos pago;
    @ManyToOne
    private HorarioReservado horarioReservado;
    @Column(name = "registroTimestamp")
    private LocalDateTime fechaRegistro;
    private Date fechaReserva;
}
