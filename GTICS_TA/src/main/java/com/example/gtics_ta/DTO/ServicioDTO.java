package com.example.gtics_ta.DTO;

import com.example.gtics_ta.Entity.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServicioDTO {
    private EspaciosDeportivos espacio;
    private CanchasFutbol cancha;
    private Piscinas piscina;
    private PistasAtletismo pista;
    private Estadios estadios;
}
