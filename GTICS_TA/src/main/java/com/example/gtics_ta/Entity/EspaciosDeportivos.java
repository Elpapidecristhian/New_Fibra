package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;

@Getter
@Setter
@Entity
@Table(name = "espaciosdeportivos")
public class EspaciosDeportivos {

    @Id
    @Column(name = "idEspacio")
    private int id;
    private String nombre;
    private String ubicacion;
    @ManyToOne
    private TipoEspacio tipoEspacio;
    @Column(name = "descripcion_corta")
    private String descripcionCorta;
    @Column(name = "descripcion_larga")
    private String descripcionLarga;
}
