package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "pagos")
public class Pagos {
    @Id
    @Column(name = "id_pagos")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "cantidad", precision = 10, scale = 2)
    private BigDecimal cantidad;

    @ManyToOne
    @JoinColumn(name = "id_medios_pago")
    private MediosPago medioPago;

    // Nuevos campos para sistema de pagos
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago")
    private EstadoPago estadoPago = EstadoPago.PENDIENTE;

    @Column(name = "fecha_pago")
    private Timestamp fechaPago;

    @Column(name = "fecha_verificacion")
    private Timestamp fechaVerificacion;

    @ManyToOne
    @JoinColumn(name = "verificado_por")
    private Usuario verificadoPor;

    @ManyToOne
    @JoinColumn(name = "id_lista_fotos_comprobantes")
    private ListaFotos listaFotosComprobantes;

    @Column(name = "numero_transaccion", length = 100)
    private String numeroTransaccion;

    @Column(name = "observaciones_admin", columnDefinition = "TEXT")
    private String observacionesAdmin;

    @Column(name = "codigo_autorizacion", length = 50)
    private String codigoAutorizacion;

    @Column(name = "datos_pasarela", columnDefinition = "JSON")
    private String datosPasarela;

    @Column(name = "ip_usuario", length = 45)
    private String ipUsuario;

    @Column(name = "user_agent")
    private String userAgent;

    // Métodos de ciclo de vida
    @PrePersist
    public void prePersist() {
        if (this.fechaPago == null) {
            this.fechaPago = new Timestamp(System.currentTimeMillis());
        }
    }

    // Enum para estado de pago
    public enum EstadoPago {
        PENDIENTE, APROBADO, RECHAZADO, PROCESANDO
    }
}
