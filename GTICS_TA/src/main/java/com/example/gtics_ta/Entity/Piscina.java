package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "piscinas")
@Data
@NoArgsConstructor
public class Piscina {

    @Id
    @Column(name = "idEspacio")
    private Integer idEspacio;

    @OneToOne
    @JoinColumn(name = "idEspacio", referencedColumnName = "idEspacio", insertable = false, updatable = false)
    private EspaciosDeportivos espacio;

    @Column(name = "tamanioPiscina")
    private String tamanioPiscina;

    @Column(name = "profundidadMin")
    private float profundidadMin;

    @Column(name = "profundidadMax")
    private float profundidadMax;

    @Column(name = "isClimatizada")
    private boolean isClimatizada;

    @Column(name = "costoHora")
    private Float costoHora;

    @Column(name = "aforo")
    private Integer aforo;

    @Column(name = "prohibido")
    private String prohibido;
}
