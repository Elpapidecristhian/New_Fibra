package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "horarioreservado")
public class HorarioReservado {
    @Id
    @Column(name = "id_horario_reservado")
    private int id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fecha;
    @ManyToOne
    @JoinColumn(name = "id_horarios")
    private Horarios horario;
    @Column(name = "is_reservado")
    private boolean isReservado;
}