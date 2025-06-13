package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "pistasatletismo")
public class PistasAtletismo {
    @Id
    @Column(name = "id_espacio")
    private int idEspacio;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_superficie")
    private TipoSuperficie tipoSuperficie;
    private float longitud;
    private String implementos;

    public enum TipoSuperficie {
        Tartan, Asfalto, Tierra
    }
}
