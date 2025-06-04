package com.example.gtics_ta.Entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "mediospago")
public class MediosPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medios_pago")
    private int id;
    @Column(name = "nombre")
    private String nombre;
}
