package com.example.gtics_ta.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "fotos")
public class Fotos {

    @Id
    @Column(name = "idFotos")  // Mantenemos el nombre original de la columna en la BD
    private Integer idFoto;

    @Lob
    private byte[] foto;

    @ManyToOne
    @JoinColumn(name = "idListaFotos")
    private ListaFotos listaFotos;

    // Getters y Setters

    public Integer getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(Integer idFoto) {
        this.idFoto = idFoto;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public ListaFotos getListaFotos() {
        return listaFotos;
    }

    public void setListaFotos(ListaFotos listaFotos) {
        this.listaFotos = listaFotos;
    }
}
