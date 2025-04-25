package com.example.gtics_ta.entity;

import jakarta.persistence.*;

@Entity
@Table(name="pagos")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPagos;
    private float cantidad;
    @ManyToOne
    @JoinColumn(name="idMediosPago",nullable=false)
    private MedioPago idMediosPago;

    public int getIdPagos() {
        return idPagos;
    }

    public void setIdPagos(int idPagos) {
        this.idPagos = idPagos;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    public MedioPago getIdMediosPago() {
        return idMediosPago;
    }

    public void setIdMediosPago(MedioPago idMediosPago) {
        this.idMediosPago = idMediosPago;
    }


}
