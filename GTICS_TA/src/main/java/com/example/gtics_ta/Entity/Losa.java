package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Setter
@Getter
@Table(name = "losa")
public class Losa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "id_espacio", referencedColumnName = "id_espacio")
    private EspaciosDeportivos espacio;

    private String tipoDeporte;
    private String tamanoCampo;
    private String iluminacion; // Puede ser "Sí" o "No"

    private Float costoHora;
    private String Prohibido;
}
