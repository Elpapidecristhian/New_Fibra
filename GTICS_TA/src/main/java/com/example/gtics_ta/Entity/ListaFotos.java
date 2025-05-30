package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "listafotos")
public class ListaFotos {
    @Id
    @Column(name = "id_lista_fotos")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToMany(mappedBy = "listaFotos", fetch = FetchType.EAGER)
    private List<Fotos> fotos;


}
