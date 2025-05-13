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
@Table(name = "piscinas")
public class Piscinas {
    @Id
    @Column(name = "id_espacio")
    private int idEspacio;
    @Column(name = "tipo_piscina")
    private String tipoPiscina;
    @Column(name = "profundidad_min")
    private float profundidadMin;
    @Column(name = "profundidad_max")
    private float profundidadMax;
    @Column(name = "is_climatizada")
    private boolean isClimatizada;
    private String requisitos;
    @Column(name = "num_carril_max")
    private int numCarrilMax;
}
