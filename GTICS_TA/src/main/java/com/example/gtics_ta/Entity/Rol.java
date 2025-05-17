package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "roles")
public class Rol implements Serializable {
    @Id
    @Column(name = "id_rol")
    private int idRol;
    private String nombre;
}
