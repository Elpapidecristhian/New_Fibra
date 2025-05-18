package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "listafotos")
public class ListaFotos {
    @Id
    @Column(name = "id_lista_fotos")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
}
