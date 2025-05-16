package com.example.gtics_ta.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Column(name = "tipo_superficie")
    private String tipoSuperficie;
    private float longitud;
    private String implementos;
}