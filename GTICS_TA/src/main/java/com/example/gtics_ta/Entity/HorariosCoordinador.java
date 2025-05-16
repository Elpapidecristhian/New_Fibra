package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "horarioscoordinador")
public class HorariosCoordinador {
    @Id
    @Column(name = "id_horarios_coordinador")
    private int id;
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "id_espacio")
    private EspaciosDeportivos espacio;
    @Column(name = "hora_entrada")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private Time horaEntrada;
    @Column(name = "hora_salida")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private Time horaSalida;
    @Column(name = "fecha_inicio")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaInicio;
    @Column(name = "fecha_fin")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaFin;
}