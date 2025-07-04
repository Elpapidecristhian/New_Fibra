package com.example.gtics_ta.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

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
    @JsonIgnore
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
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaAbre;
    @Column(name = "hora_cierra")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaCierra;
    private Integer aforo;
    private boolean operativo;
    @Column(name = "costo_horario")
    private float costoHorario;
    @ManyToOne
    @JoinColumn(name = "id_lista_fotos")
    @JsonIgnore
    private ListaFotos listaFotos;

    // Campos de geolocalización
    @Column(name = "latitud", precision = 10, scale = 8)
    private BigDecimal latitud;

    @Column(name = "longitud", precision = 11, scale = 8)
    private BigDecimal longitud;

    @Column(name = "radio_cobertura")
    private Integer radioCobertura = 100;

    @OneToMany(mappedBy = "espacioDeportivo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<Reservas> listaReservas;

    @OneToMany(mappedBy = "espacio", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<Horarios> listaHorarios;
}
