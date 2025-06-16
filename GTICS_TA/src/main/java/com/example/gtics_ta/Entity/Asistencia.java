package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "asistencia")
public class Asistencia {
    @Id
    @Column(name = "id_asistencia")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    // Nuevos campos para control de asistencia
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_asistencia")
    private EstadoAsistencia estadoAsistencia = EstadoAsistencia.FALTA;

    @Column(name = "minutos_retraso")
    private Integer minutosRetraso = 0;

    @Column(name = "observaciones")
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "registrado_por")
    private Usuario registradoPor;

    @Column(name = "fecha_registro")
    private Timestamp fechaRegistro;

    @ManyToOne
    @JoinColumn(name = "id_horarios_coordinador")
    private HorariosCoordinador horariosCoordinador;

    // Enum para estado de asistencia
    public enum EstadoAsistencia {
        A_TIEMPO, TARDE, FALTA, JUSTIFICADO
    }
}
