package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "gimnasios")
public class Gimnasios {
    @Id
    @Column(name = "id_espacio")
    private Integer idEspacios;
    @Column(name = "cantidad_maquinas")
    private int cantidadMaquinas;
    @Column(name = "tipos_maquinas")
    private String tiposMaquinas;
    @Column(name = "tiene_sauna")
    private Boolean tieneSauna;
    @Column(name = "tiene_duchas")
    private Boolean tieneDuchas;
    @Column(name = "costo_semanal")
    private float costoSemanal;
    @Column(name = "costo_mensual")
    private float costoMensual;
    @Column(name = "costo_anual")
    private float costoAnual;
}