package com.example.gtics_ta.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "tipoespacio")
public class TipoEspacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTipoEspacio;

    @Column(name = "nombreTipo")
    private String nombreTipo;

    // Getters y Setters


    public Integer getIdTipoEspacio() {
        return idTipoEspacio;
    }

    public void setIdTipoEspacio(Integer idTipoEspacio) {
        this.idTipoEspacio = idTipoEspacio;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }
}
