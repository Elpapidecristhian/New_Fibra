package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "fotos")
public class Fotos {
    @Id
    @Column(name = "id_fotos")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "id_lista_fotos")
    private ListaFotos listaFotos;
    @Column(name = "foto_nombre")
    private String fotoNombre;
    @Column(name = "foto_tipo_archivo")
    private String fotoTipoArchivo;

    // Cambio: Almacenar URL de S3 en lugar de BLOB
    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    // Mantener campo BLOB temporalmente para migración
    @Lob
    @Column(name = "foto")
    private byte[] foto;
}
