package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "canchasfutbol")
public class CanchasFutbol {
    @Id
    @Column(name = "id_espacio")
    private int idEspacio;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_superficie")
    private TipoSuperficieFutbol tipoSuperficie;
    @Column(name = "iluminacion_nocturna")
    private boolean iluminacionNocturna;
    @Column(name = "balones_disponibles")
    private boolean balonesDisponibles;
    private float ancho;
    private float alto;

    public enum TipoSuperficieFutbol {
        Grass, Losa
    }
}
