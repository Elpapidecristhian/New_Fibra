package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;

@Entity
@Getter
@Setter
@Table(name = "horarios")
public class Horarios {
    @Id
    @Column(name = "idHorarios")
    private int id;
    private Time horaInicio;
    private Time horaFin;
    private int idEspacio;
}
