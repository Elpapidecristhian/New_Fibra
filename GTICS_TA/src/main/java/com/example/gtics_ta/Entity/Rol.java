package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "roles")
public class Rol {
    @Id
    @Column(name = "idRol")
    private int idRol;
    @Column(name = "rolNombre")
    private String nombre;
}
