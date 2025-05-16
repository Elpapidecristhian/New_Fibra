package com.example.gtics_ta.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "listafotos")
public class ListaFotos {
    @Id
    @Column(name = "id_lista_fotos")
    private int id;
}