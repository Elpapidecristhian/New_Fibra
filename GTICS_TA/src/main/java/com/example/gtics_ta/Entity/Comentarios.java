package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "comentarios")
public class Comentarios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "fecha_creacion")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;

    // Constructor por defecto
    public Comentarios() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Constructor con parámetros
    public Comentarios(EspaciosDeportivos espacio, Usuario usuario, TipoComentario tipoComentario, String contenido) {
        this.espacio = espacio;
        this.usuario = usuario;
        this.tipoComentario = tipoComentario;
        this.contenido = contenido;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Método para establecer la fecha antes de persistir
    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }
}