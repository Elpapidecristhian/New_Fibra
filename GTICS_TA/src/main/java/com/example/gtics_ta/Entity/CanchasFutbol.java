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
@Table(name = "canchasfutbol")
public class CanchasFutbol {
    @Id
    @Column(name = "id_espacio")
    private int idEspacio;
    @Column(name = "tipo_superficie")
    private String tipoSuperficie;
    @Column(name = "iluminacion_nocturna")
    private boolean ilumacionNocturna;
    @Column(name = "balones_disponibles")
    private boolean balonesDisponibles;
    private float ancho;
    private float alto;
}
