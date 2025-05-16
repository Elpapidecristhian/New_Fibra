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
    @Column(name = "id_horarios")
    private int id;
    @Column(name = "hora_inicio")
    private Time horaInicio;
    @Column(name = "hora_fin")
    private Time horaFin;
    @Column(name = "id_espacio")
    private int idEspacio;
}
