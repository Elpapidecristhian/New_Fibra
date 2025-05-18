package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @NotBlank(message="Los nombres on obligatorios")
    @Size(max = 50, message = "Los nombres no pueden exceder los 50 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 50, message = "Los apellidos no pueden exceder los 50 caracteres")
    private String apellidos;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Debe ser un correo electrónico válido")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String contrasenia;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotNull(message = "El DNI es obligatorio")
    @Digits(integer = 8, fraction = 0, message = "El DNI debe tener 8 dígitos")
    private Integer dni;

    @Column(name = "num_celular")
    @NotNull(message = "El número celular es obligatorio")
    @Positive
    @Digits(integer = 9, fraction = 0, message = "Debe ingresar un número de 9 dígitos")
    private Integer numCelular;
    @ManyToOne
    @JoinColumn(name = "id_rol")
    private Rol rol;
    private boolean activo =true;
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
