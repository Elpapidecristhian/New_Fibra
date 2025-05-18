package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "horarioreservado")
public class HorarioReservado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario_reservado")
    private Integer id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    @ManyToOne
    @JoinColumn(name = "id_horarios")
    private Horarios horario;
    @Column(name = "is_reservado")
    private boolean isReservado;

    @Column(name = "estado", nullable = false)
    private String estado = "Disponible";

}