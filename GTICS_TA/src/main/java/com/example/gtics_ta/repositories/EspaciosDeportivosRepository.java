package com.example.gtics_ta.repositories;

import com.example.gtics_ta.Entity.EspaciosDeportivos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspaciosDeportivosRepository extends JpaRepository<EspaciosDeportivos, Integer> {

    @Query("SELECT ed.id, te.nombre, ed.nombre, ed.ubicacion, ed.correoContacto, " +
            "CONCAT(h.horaInicio, ' - ', h.horaFin), ed.aforo " +
            "FROM EspaciosDeportivos ed " +
            "JOIN ed.tipoEspacio te " +  // Relación con TipoEspacio
            "JOIN ed.horarios h")      // Relación con Horarios
    List<Object[]> findEspaciosConTipoYHorario();
}
