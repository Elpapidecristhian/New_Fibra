package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "pagos")
public class Pagos {
    @Id
    @Column(name = "id_pagos")
    private int id;
    private float cantidad;
    @ManyToOne
    @JoinColumn(name = "id_medios_pago")
    private MediosPago medioPago;
}