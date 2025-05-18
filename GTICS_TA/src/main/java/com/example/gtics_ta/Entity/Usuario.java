package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "usuario")
public class Usuario implements Serializable {

    @Id
    @Column(name = "id_usuario")
    private int id;
    private String nombres;
    private String apellidos;
    private String correo;
    private String contrasenia;
    private String direccion;
    private Integer dni;
    @Column(name = "num_celular")
    @Positive
    @Digits(integer = 9, fraction = 0, message = "Debe ingresar un número de 9 dígitos")
    private Integer numCelular;
    @ManyToOne
    @JoinColumn(name = "id_rol")
    private Rol rol;
    private boolean activo;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    @Lob
    private byte[] foto;
    @Column(name = "foto_nombre")
    private String fotoNombre;
    @Column(name = "foto_tipo_archivo")
    private String fotoTipoArchivo;
}
