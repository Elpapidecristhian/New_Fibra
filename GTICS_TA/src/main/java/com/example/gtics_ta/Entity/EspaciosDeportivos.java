package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.beans.ConstructorProperties;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Timer;

@Getter
@Setter
@Entity
@Table(name = "espaciosdeportivos")
public class EspaciosDeportivos {

    @Id
    @Column(name = "id_espacio")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    private String ubicacion;
    @ManyToOne
    @JoinColumn(name = "id_tipo_espacio")
    private TipoEspacio tipoEspacio;
    @Column(name = "descripcion_corta")
    private String descripcionCorta;
    @Column(name = "descripcion_larga")
    private String descripcionLarga;
    @Column(name = "num_contacto")
    private int numContacto;
    @Column(name = "correo_contacto")
    private String correoContacto;
    @Column(name = "maps_url")
    private String mapsUrl;
    @Column(name = "hora_abre")
    private LocalTime horaAbre;
    @Column(name = "hora_cierra")
    private LocalTime horaCierra;
    private Integer aforo;
    private boolean operativo;
    @Column(name = "costo_horario")
    private float costoHorario;
    @Column(name = "id_lista_fotos")
    private Integer idListaFotos;
}
