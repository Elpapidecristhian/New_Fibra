package com.example.gtics_ta.entity;

import jakarta.persistence.*;

@Entity
@Table(name="espaciosdeportivos")
public class EspaciosDeportivos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idEspacio;
    private String nombre;
    private String ubicación;
    @ManyToOne
    @JoinColumn(name="idTipoEspacio",nullable=false)
    private TipoEspacio idTipoEspacio;
    @ManyToOne
    @JoinColumn(name="idListaFotos",nullable=false)
    private ListaFotos idListaFotos;
    private String descripcion_corta;
    private String descripcion_larga;

    public int getIdEspacio() {
        return idEspacio;
    }

    public void setIdEspacio(int idEspacio) {
        this.idEspacio = idEspacio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicación() {
        return ubicación;
    }

    public void setUbicación(String ubicación) {
        this.ubicación = ubicación;
    }

    public TipoEspacio getIdTipoEspacio() {
        return idTipoEspacio;
    }

    public void setIdTipoEspacio(TipoEspacio idTipoEspacio) {
        this.idTipoEspacio = idTipoEspacio;
    }

    public ListaFotos getIdListaFotos() {
        return idListaFotos;
    }

    public void setIdListaFotos(ListaFotos idListaFotos) {
        this.idListaFotos = idListaFotos;
    }

    public String getDescripcion_corta() {
        return descripcion_corta;
    }

    public void setDescripcion_corta(String descripcion_corta) {
        this.descripcion_corta = descripcion_corta;
    }

    public String getDescripcion_larga() {
        return descripcion_larga;
    }

    public void setDescripcion_larga(String descripcion_larga) {
        this.descripcion_larga = descripcion_larga;
    }


}
