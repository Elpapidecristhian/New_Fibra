package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "asistencia")
public class Asistencia {
    @Id
    @Column(name = "id_asistencia")
    private int id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fecha;
    @Column(name = "horas_trabajadas")
    private String horasTrabajadas;
    @Column(name = "hora_entrada")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private Time horaEntrada;
    @Column(name = "hora_salida")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private Time horaSalida;
    @ManyToOne
    @JoinColumn(name = "id_horarios_coordinador")
    private HorariosCoordinador horariosCoordinador;
}