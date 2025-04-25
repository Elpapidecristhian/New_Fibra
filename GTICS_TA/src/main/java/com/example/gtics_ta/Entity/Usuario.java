package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
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
    private Integer dni;
    private Integer numCelular;
    @ManyToOne
    @JoinColumn(name = "idRol")
    private Rol rol;
    private boolean isBaneado;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;
}
