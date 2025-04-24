package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "usuario")
public class Usuario {

    @Id
    @Column(name = "idUsuario")
    private int id;
    private String nombres;
    private String apellidos;
    private String correo;
    private String contrasenia;
    private String direccion;
    private int dni;
    private int numCelular;
    @ManyToOne
    private Rol rol;
    private boolean isBaneado;
    private Date fechaNacimiento;
}
