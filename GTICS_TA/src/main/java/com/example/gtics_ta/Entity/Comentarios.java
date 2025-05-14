package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "comentarios")
public class Comentarios {
    @Id
    @Column(name = "id_comentarios")
    private int id;
    @ManyToOne
    @JoinColumn(name = "id_espacio")
    private EspaciosDeportivos espacio;
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "id_tipo_comentario")
    private TipoComentario tipoComentario;
    private String contenido;
}
