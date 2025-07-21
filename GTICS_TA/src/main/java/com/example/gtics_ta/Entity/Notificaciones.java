package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Entity
@Getter
@Setter
@Table(name = "notificaciones")
public class Notificaciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_notificacion")
    private TipoNotificacion tipoNotificacion;

    @Column(name = "titulo", length = 100)
    private String titulo;

    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "leida")
    private Boolean leida = false;

    @Column(name = "fecha_creacion")
    private Timestamp fechaCreacion;

    @Column(name = "fecha_lectura")
    private Timestamp fechaLectura;

    @ManyToOne
    @JoinColumn(name = "id_reserva")
    private Reservas reserva;

    @ManyToOne
    @JoinColumn(name = "id_suscripcion")
    private Suscripciones suscripcion;

    @ManyToOne
    @JoinColumn(name = "id_mantenimiento")
    private Mantenimiento mantenimiento;

    // Constructor por defecto
    public Notificaciones() {
        this.fechaCreacion = new Timestamp(System.currentTimeMillis());
        this.leida = false;
    }

    // Constructor con parámetros
    public Notificaciones(Usuario usuario, TipoNotificacion tipoNotificacion, String titulo, String mensaje) {
        this.usuario = usuario;
        this.tipoNotificacion = tipoNotificacion;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fechaCreacion = new Timestamp(System.currentTimeMillis());
        this.leida = false;
    }

    // Constructor con reserva
    public Notificaciones(Usuario usuario, TipoNotificacion tipoNotificacion, String titulo, String mensaje, Reservas reserva) {
        this(usuario, tipoNotificacion, titulo, mensaje);
        this.reserva = reserva;
    }

    public Notificaciones(Usuario usuario, TipoNotificacion tipoNotificacion, String titulo, String mensaje, Suscripciones suscripcion) {
        this(usuario, tipoNotificacion, titulo, mensaje);
        this.suscripcion = suscripcion;
    }

    // Constructor con mantenimiento
    public Notificaciones(Usuario usuario, TipoNotificacion tipoNotificacion, String titulo, String mensaje, Mantenimiento mantenimiento) {
        this(usuario, tipoNotificacion, titulo, mensaje);
        this.mantenimiento = mantenimiento;
    }

    // Método para marcar como leída
    public void marcarComoLeida() {
        this.leida = true;
        this.fechaLectura = new Timestamp(System.currentTimeMillis());
    }

    // Método para establecer la fecha antes de persistir
    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = new Timestamp(System.currentTimeMillis());
        }
    }

    // Enum para tipo de notificación
    public enum TipoNotificacion {
        CANCELACION_RESERVA,
        CANCELACION_SUSCRIPCION,
        MANTENIMIENTO_PROGRAMADO,
        RECORDATORIO_RESERVA,
        RECORDATORIO_SUSCRIPCION,
        CAMBIO_HORARIO,
        PROMOCION
    }
    //Metodo para ofmratear la fecha
    public String getFechaFormateada() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(this.fechaCreacion);
    }
}
