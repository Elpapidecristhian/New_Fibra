package com.example.gtics_ta.repository;

import com.example.gtics_ta.entity.TipoEspacio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoEspacioRepository extends JpaRepository<TipoEspacio, Integer> {
    // Método para buscar TipoEspacio por nombre
    TipoEspacio findByNombreTipo(String nombreTipo);
}
