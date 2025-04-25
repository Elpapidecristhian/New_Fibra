package com.example.gtics_ta.entity;

import jakarta.persistence.*;

@Entity
@Table(name="listafotos")
public class ListaFotos {

    public int getIdListaFotos() {
        return idListaFotos;
    }

    public void setIdListaFotos(int idListaFotos) {
        this.idListaFotos = idListaFotos;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idListaFotos;
}
