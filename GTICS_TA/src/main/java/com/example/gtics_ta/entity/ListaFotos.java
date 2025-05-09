package com.example.gtics_ta.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "listafotos")
public class ListaFotos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idListaFotos;

    @OneToMany(mappedBy = "listaFotos")
    private List<Fotos> fotos;

    // Getters y Setters

    public Integer getIdListaFotos() {
        return idListaFotos;
    }

    public void setIdListaFotos(Integer idListaFotos) {
        this.idListaFotos = idListaFotos;
    }

    public List<Fotos> getFotos() {
        return fotos;
    }

    public void setFotos(List<Fotos> fotos) {
        this.fotos = fotos;
    }
}
