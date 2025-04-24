package com.example.gtics_ta.Entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "mediospago")
public class MediosPago {
    @Id
    @Column(name = "idMediosPago")
    private int id;
    private String medioPago;
}
