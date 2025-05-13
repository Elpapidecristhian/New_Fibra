package com.example.gtics_ta.Entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "mediospago")
public class MediosPago {
    @Id
    @Column(name = "id_medios_pago")
    private int id;
    private String nombre;
}
