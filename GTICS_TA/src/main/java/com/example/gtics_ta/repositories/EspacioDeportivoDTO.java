package com.example.gtics_ta.repositories;

import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor


public class EspacioDeportivoDTO {

    private Integer idEspacio;
    private String tipoEspacio;
    private String nombreEspacio;
    private String ubicacion;
    private String correoContacto;
    private String horarioDisponible;
    private Integer aforo;

    // Constructor
    public EspacioDeportivoDTO(Integer idEspacio, String tipoEspacio, String nombreEspacio,
                               String ubicacion, String correoContacto, String horarioDisponible, Integer aforo) {
        this.idEspacio = idEspacio;
        this.tipoEspacio = tipoEspacio;
        this.nombreEspacio = nombreEspacio;
        this.ubicacion = ubicacion;
        this.correoContacto = correoContacto;
        this.horarioDisponible = horarioDisponible;
        this.aforo = aforo;
    }

    // Getters y setters (con Lombok, puedes usar @Getter y @Setter)
}
