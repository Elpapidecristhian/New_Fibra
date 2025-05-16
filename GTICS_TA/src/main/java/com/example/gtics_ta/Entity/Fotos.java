package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "fotos")
public class Fotos {
    @Id
    @Column(name = "id_fotos")
    private int id;
    @Column(name = "id_lista_fotos")
    private int idListaFotos;
}