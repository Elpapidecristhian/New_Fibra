package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.TipoEspacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoEspacioRepository extends JpaRepository<TipoEspacio, Integer> {
}
