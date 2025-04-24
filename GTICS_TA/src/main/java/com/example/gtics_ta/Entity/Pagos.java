package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "pagos")
public class Pagos {
    @Id
    @Column(name = "idPagos")
    private int id;
    private float cantidad;
    @ManyToOne
    private MediosPago medioPago;
}
