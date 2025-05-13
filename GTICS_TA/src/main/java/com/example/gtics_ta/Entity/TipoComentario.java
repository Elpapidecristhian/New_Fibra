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
@Table(name = "tipocomentario")
public class TipoComentario {
    @Id
    @Column(name = "id_tipo_comentario")
    private int id;
    private String nombre;
}
