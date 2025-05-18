package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "espaciosdeportivos")
public class EspaciosDeportivos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_espacio")
    private int id;
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
    private Integer aforo;
    private boolean operativo;
    @Column(name = "costo_horario")
    private float costoHorario;

    @Column(name = "hora_abre")
    private LocalTime horaAbre;

    @Column(name = "hora_cierra")
    private LocalTime horaCierra;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_lista_fotos", nullable = false)
    private ListaFotos listaFotos;



}