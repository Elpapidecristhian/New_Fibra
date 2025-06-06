package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "historial_pagos")
public class HistorialPagos {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial_pago")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "id_pago")
    private Pagos pago;
    
    @Column(name = "estado_anterior", length = 50)
    private String estadoAnterior;
    
    @Column(name = "estado_nuevo", length = 50)
    private String estadoNuevo;
    
    @Column(name = "motivo")
    private String motivo;
    
    @ManyToOne
    @JoinColumn(name = "cambiado_por")
    private Usuario cambiadoPor;
    
    @Column(name = "fecha_cambio")
    private Timestamp fechaCambio;
    
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
    
    // Constructor por defecto
    public HistorialPagos() {
        this.fechaCambio = new Timestamp(System.currentTimeMillis());
    }
    
    // Constructor con parámetros
    public HistorialPagos(Pagos pago, String estadoAnterior, String estadoNuevo, String motivo, Usuario cambiadoPor) {
        this.pago = pago;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.motivo = motivo;
        this.cambiadoPor = cambiadoPor;
        this.fechaCambio = new Timestamp(System.currentTimeMillis());
    }
    
    // Método para establecer la fecha antes de persistir
    @PrePersist
    public void prePersist() {
        if (this.fechaCambio == null) {
            this.fechaCambio = new Timestamp(System.currentTimeMillis());
        }
    }
}
