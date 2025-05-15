package com.example.gtics_ta.repositories;

import com.example.gtics_ta.Entity.TipoEspacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoEspacioRepository extends JpaRepository<TipoEspacio, Integer> {
    TipoEspacio findByNombre(String nombre);
}
