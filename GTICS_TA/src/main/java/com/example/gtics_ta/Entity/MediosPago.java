package com.example.gtics_ta.Entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "mediospago")
public class MediosPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medios_pago")
    private int id;

    @Column(name = "nombre")
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago")
    private TipoPago tipoPago;

    @Column(name = "requiere_verificacion")
    private Boolean requiereVerificacion = false;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "datos_cuenta", columnDefinition = "TEXT")
    private String datosCuenta;

    @Column(name = "icono")
    private String icono;

    // Enum para tipo de pago
    public enum TipoPago {
        AUTOMATICO, MANUAL
    }
}
