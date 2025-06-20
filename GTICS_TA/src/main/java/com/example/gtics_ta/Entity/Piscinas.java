package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
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
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_piscina")
    private TipoPiscina tipoPiscina;
    @Column(name = "profundidad_min")
    private float profundidadMin;
    @Column(name = "profundidad_max")
    private float profundidadMax;
    @Column(name = "is_climatizada")
    private boolean Climatizada;
    private String requisitos;
    @Column(name = "num_carril_max")
    private int numCarrilMax;

    public enum TipoPiscina {
        Olimpica, Publica
    }
}
