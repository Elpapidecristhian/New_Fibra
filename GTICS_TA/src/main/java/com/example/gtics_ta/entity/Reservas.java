package com.example.gtics_ta.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="reservas")
public class Reservas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idReservas;
    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario idUsuario;
    @ManyToOne
    @JoinColumn(name = "idEspacio", nullable = false)
    private EspaciosDeportivos idEspacio;
    @ManyToOne
    @JoinColumn(name = "Pagos_idPagos", nullable = false)
    private Pago Pagos_idPagos;
    @CreationTimestamp
    private LocalDateTime registro;
    @CreationTimestamp
    private LocalDate fechaReserva;

    public int getIdReservas() {
        return idReservas;
    }

    public void setIdReservas(int idReservas) {
        this.idReservas = idReservas;
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

    public Pago getPagos_idPagos() {
        return Pagos_idPagos;
    }

    public void setPagos_idPagos(Pago pagos_idPagos) {
        Pagos_idPagos = pagos_idPagos;
    }

    public LocalDateTime getRegistro() {
        return registro;
    }

    public void setRegistro(LocalDateTime registro) {
        this.registro = registro;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }





}
