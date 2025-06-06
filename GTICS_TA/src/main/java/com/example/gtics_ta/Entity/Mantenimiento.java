package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "mantenimiento")
public class Mantenimiento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mantenimiento")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "id_espacio")
    private EspaciosDeportivos espacio;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_mantenimiento")
    private TipoMantenimiento tipoMantenimiento;
    
    @Column(name = "titulo", length = 100)
    private String titulo;
    
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "fecha_inicio")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;
    
    @Column(name = "fecha_fin")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;
    
    @Column(name = "hora_inicio")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private Time horaInicio;
    
    @Column(name = "hora_fin")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private Time horaFin;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_mantenimiento")
    private EstadoMantenimiento estadoMantenimiento = EstadoMantenimiento.PROGRAMADO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad")
    private Prioridad prioridad = Prioridad.MEDIA;
    
    @Column(name = "costo_estimado", precision = 10, scale = 2)
    private BigDecimal costoEstimado;
    
    @Column(name = "costo_real", precision = 10, scale = 2)
    private BigDecimal costoReal;
    
    @Column(name = "empresa_encargada", length = 100)
    private String empresaEncargada;
    
    @Column(name = "contacto_encargado", length = 50)
    private String contactoEncargado;
    
    @Column(name = "requiere_cierre_total")
    private Boolean requiereCierreTotal = true;
    
    @Column(name = "afecta_horarios_especificos")
    private Boolean afectaHorariosEspecificos = false;
    
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
    
    @ManyToOne
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;
    
    @Column(name = "fecha_creacion")
    private Timestamp fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private Timestamp fechaActualizacion;
    
    @Column(name = "reservas_canceladas")
    private Integer reservasCanceladas = 0;
    
    @Column(name = "notificaciones_enviadas")
    private Boolean notificacionesEnviadas = false;
    
    // Constructor por defecto
    public Mantenimiento() {
        this.fechaCreacion = new Timestamp(System.currentTimeMillis());
        this.fechaActualizacion = new Timestamp(System.currentTimeMillis());
    }
    
    // Constructor con parámetros básicos
    public Mantenimiento(EspaciosDeportivos espacio, TipoMantenimiento tipoMantenimiento, String titulo, String descripcion, LocalDate fechaInicio, LocalDate fechaFin, Usuario creadoPor) {
        this();
        this.espacio = espacio;
        this.tipoMantenimiento = tipoMantenimiento;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.creadoPor = creadoPor;
    }
    
    // Métodos de ciclo de vida
    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = new Timestamp(System.currentTimeMillis());
        }
        this.fechaActualizacion = new Timestamp(System.currentTimeMillis());
    }
    
    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = new Timestamp(System.currentTimeMillis());
    }
    
    // Métodos de utilidad
    public boolean esMantenimientoCompleto() {
        return this.requiereCierreTotal;
    }
    
    public boolean estaEnProgreso() {
        return this.estadoMantenimiento == EstadoMantenimiento.EN_PROGRESO;
    }
    
    public boolean estaCompletado() {
        return this.estadoMantenimiento == EstadoMantenimiento.COMPLETADO;
    }
    
    // Enums
    public enum TipoMantenimiento {
        PREVENTIVO, CORRECTIVO, EMERGENCIA, MEJORA, LIMPIEZA_PROFUNDA
    }
    
    public enum EstadoMantenimiento {
        PROGRAMADO, EN_PROGRESO, COMPLETADO, CANCELADO, POSPUESTO
    }
    
    public enum Prioridad {
        BAJA, MEDIA, ALTA, CRITICA
    }
}
