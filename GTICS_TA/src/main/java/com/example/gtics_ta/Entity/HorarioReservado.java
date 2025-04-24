package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Getter
@Table(name = "horarioreservado")
public class HorarioReservado {
    @Id
    @Column(name = "idHorarioReservado")
    private int id;
    private Date fecha;
    @ManyToOne
    @JoinColumn(name = "idHorarios")
    private Horarios horario;
}
