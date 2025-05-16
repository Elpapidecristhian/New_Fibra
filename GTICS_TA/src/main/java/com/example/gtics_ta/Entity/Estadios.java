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
@Table(name = "estadios")
public class Estadios {
    @Id
    @Column(name = "id_espacio")
    private int idEspacio;
    private int aforo;
    @Column(name = "uso_permitido")
    private String usoPermitido;
    @Column(name = "seguridad_disponible")
    private boolean seguridadDisponible;
    @Column(name = "sonido_pantallas_disponible")
    private boolean sonidoPantallasDisponible;
    @Column(name = "iluminacion_profesional_disponible")
    private boolean iluminacionProfesionalDisponible;
}
