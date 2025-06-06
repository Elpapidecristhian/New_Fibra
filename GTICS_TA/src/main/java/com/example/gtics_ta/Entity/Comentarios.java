package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
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

    // Cambio: Usar ENUM en lugar de relación con TipoComentario
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comentario")
    private TipoComentario tipoComentario = TipoComentario.COMENTARIO;

    @Column(columnDefinition = "TEXT")
    private String contenido;

    // Nuevos campos para sistema de comentarios mejorado
    @ManyToOne
    @JoinColumn(name = "id_lista_fotos")
    private ListaFotos listaFotos;

    @Column(name = "fecha_creacion")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private Timestamp fechaActualizacion;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "revisado_por_admin")
    private Boolean revisadoPorAdmin = false;

    @Column(name = "requiere_mantenimiento")
    private Boolean requiereMantenimiento = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad_mantenimiento")
    private PrioridadMantenimiento prioridadMantenimiento;

    @Column(name = "notas_admin", columnDefinition = "TEXT")
    private String notasAdmin;

    @Column(name = "fecha_revision")
    private Timestamp fechaRevision;

    @ManyToOne
    @JoinColumn(name = "revisado_por")
    private Usuario revisadoPor;

    @ManyToOne
    @JoinColumn(name = "id_mantenimiento_generado")
    private Mantenimiento mantenimientoGenerado;

    // Constructor por defecto
    public Comentarios() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Constructor con parámetros actualizado
    public Comentarios(EspaciosDeportivos espacio, Usuario usuario, TipoComentario tipoComentario, String contenido) {
        this.espacio = espacio;
        this.usuario = usuario;
        this.tipoComentario = tipoComentario;
        this.contenido = contenido;
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
        this.revisadoPorAdmin = false;
        this.requiereMantenimiento = false;
    }

    // Método para establecer la fecha antes de persistir
    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        if (this.fechaActualizacion == null) {
            this.fechaActualizacion = new Timestamp(System.currentTimeMillis());
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = new Timestamp(System.currentTimeMillis());
    }

    // Enums
    public enum TipoComentario {
        COMENTARIO, REPARACION
    }

    public enum PrioridadMantenimiento {
        BAJA, MEDIA, ALTA, CRITICA
    }
}
