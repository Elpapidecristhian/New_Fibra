package com.example.gtics_ta.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="asistencia")
public class Asistencia {
    public int getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(int idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getHora() {
        return hora;
    }

    public void setHora(LocalDateTime hora) {
        this.hora = hora;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
    }

    public EspaciosDeportivos getIdEspacio() {
        return idEspacio;
    }

    public void setIdEspacio(EspaciosDeportivos idEspacio) {
        this.idEspacio = idEspacio;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAsistencia;
    private LocalDate fecha;
    private LocalDateTime hora;
    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario idUsuario;
    @ManyToOne
    @JoinColumn(name = "idEspacio", nullable = false)
    private EspaciosDeportivos idEspacio;
}
