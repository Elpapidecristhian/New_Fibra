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
    @Column(name = "id_usuario")
    private int id;
    private String nombres;
    private String apellidos;
    @Column(name="correo")
    private String username;
    @Column(name="contrasenia")
    private String password;
    private String direccion;
    private Integer dni;
    @Column(name = "num_celular")
    private Integer numCelular;
    @ManyToOne
    @JoinColumn(name = "id_rol")
    private Rol rol;
    @Column(name = "is_baneado")
    private boolean isBaneado;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    @Lob
    @Column(name = "foto")
    private byte[] foto;
}
