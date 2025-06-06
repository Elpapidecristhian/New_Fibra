package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "historial_reservas")
public class HistorialReservas {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "id_reserva")
    private Reservas reserva;
    
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
    
    // Constructor por defecto
    public HistorialReservas() {
        this.fechaCambio = new Timestamp(System.currentTimeMillis());
    }
    
    // Constructor con parámetros
    public HistorialReservas(Reservas reserva, String estadoAnterior, String estadoNuevo, String motivo, Usuario cambiadoPor) {
        this.reserva = reserva;
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
