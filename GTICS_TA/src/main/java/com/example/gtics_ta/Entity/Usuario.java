package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombres")  // Asegúrate de mapear los nombres correctamente
    private String nombres;

    @Column(name = "apellidos")  // Asegúrate de mapear los apellidos correctamente
    private String apellidos;

    @Column(name = "dni")  // Mapear el DNI
    private Integer dni;

    @Column(name = "num_Celular")  // Mapea la columna "numCelular"
    private Integer numCelular;

    @Column(name = "correo")  // Mapear el email
    private String correo;


    @Column(name = "direccion")  // Mapear dirección
    private String direccion;

    @Column(name = "fechaNacimiento")  // Mapear fecha de nacimiento
    private Date fechaNacimiento;

    @Column(name = "fotoPerfil")  // Mapear foto de perfil
    private byte[] fotoPerfil;


}
