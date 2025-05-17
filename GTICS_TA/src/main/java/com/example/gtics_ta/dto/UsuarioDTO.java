package com.example.gtics_ta.dto;

import java.time.LocalDate;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Integer id;
    private String nombres;
    private String apellidos;
    private String username;
    private String direccion;
    private Integer dni;
    private Integer numCelular;
    private byte[] foto;
    private LocalDate fechaNacimiento;
}
