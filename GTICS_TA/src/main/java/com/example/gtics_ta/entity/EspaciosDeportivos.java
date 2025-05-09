package com.example.gtics_ta.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "espaciosdeportivos")
public class EspaciosDeportivos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEspacio;

    private String nombre;
    private String ubicacion;

    @Column(name = "descripcion_corta")
    private String descripcionCorta;

    @Column(name = "descripcion_larga")
    private String descripcionLarga;

    @ManyToOne
    @JoinColumn(name = "idTipoEspacio")
    private TipoEspacio tipoEspacio;

    @ManyToOne
    @JoinColumn(name = "idListaFotos")
    private ListaFotos listaFotos;

    // Getters y Setters


    public Integer getIdEspacio() {
        return idEspacio;
    }

    public void setIdEspacio(Integer idEspacio) {
        this.idEspacio = idEspacio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDescripcionCorta() {
        return descripcionCorta;
    }

    public void setDescripcionCorta(String descripcionCorta) {
        this.descripcionCorta = descripcionCorta;
    }

    public String getDescripcionLarga() {
        return descripcionLarga;
    }

    public void setDescripcionLarga(String descripcionLarga) {
        this.descripcionLarga = descripcionLarga;
    }

    public TipoEspacio getTipoEspacio() {
        return tipoEspacio;
    }

    public void setTipoEspacio(TipoEspacio tipoEspacio) {
        this.tipoEspacio = tipoEspacio;
    }

    public ListaFotos getListaFotos() {
        return listaFotos;
    }

    public void setListaFotos(ListaFotos listaFotos) {
        this.listaFotos = listaFotos;
    }
}
