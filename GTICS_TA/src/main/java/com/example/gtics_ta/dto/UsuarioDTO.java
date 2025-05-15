package com.example.gtics_ta.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Integer idUsuario;
    private String nombres;
    private String apellidos;
    private String correo;
    private String direccion;
    private Integer dni;
    private Integer numCelular;
    private byte[] fotoPerfil;
    private Date fechaNacimiento;
}
