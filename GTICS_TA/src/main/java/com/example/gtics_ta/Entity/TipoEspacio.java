package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tipoespacio")
public class TipoEspacio {
    @Id
    @Column(name = "id_tipo_espacio")
    private int id;
    private String nombre;


}