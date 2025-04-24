package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
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
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "idEspacio")
    private EspaciosDeportivos espacioDeportivo;
    @ManyToOne
    @JoinColumn(name = "Pagos_idPagos")
    private Pagos pago;
    @ManyToOne
    @JoinColumn(name = "idHorarioReservado")
    private HorarioReservado horarioReservado;
    @Column(name = "registroTimestamp")
    private Timestamp fechaRegistro;
    private Date fechaReserva;
}
