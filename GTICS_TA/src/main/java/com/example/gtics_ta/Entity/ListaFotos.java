package com.example.gtics_ta.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "listafotos")
@Getter
@Setter
public class ListaFotos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lista_fotos")
    private Integer id;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;


    @OneToMany(mappedBy = "listaFotos")
    private List<EspaciosDeportivos> espaciosDeportivos;
}
