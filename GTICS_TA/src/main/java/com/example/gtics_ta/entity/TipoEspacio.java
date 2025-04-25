package com.example.gtics_ta.entity;

import jakarta.persistence.*;

@Entity
@Table(name="tipoespacio")
public class TipoEspacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTipoEspacio;

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public int getIdTipoEspacio() {
        return idTipoEspacio;
    }

    public void setIdTipoEspacio(int idTipoEspacio) {
        this.idTipoEspacio = idTipoEspacio;
    }

    private String nombreTipo;
}
