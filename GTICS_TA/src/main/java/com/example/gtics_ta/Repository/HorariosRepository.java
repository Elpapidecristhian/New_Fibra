package com.example.gtics_ta.Repository;

import com.example.gtics_ta.DTO.HorariosConsultaDTO;
import com.example.gtics_ta.Entity.Horarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HorariosRepository extends JpaRepository<Horarios, Integer> {

    List<Horarios> findByIdEspacio(Integer idEspacio);

    @Query(value = "SELECT h.*, IF(hr.id_horario_reservado IS NULL, false, true) AS reservado " +
                    "FROM horarios h " +
                    "LEFT JOIN horarioreservado hr ON h.id_horarios = hr.id_horarios AND hr.fecha = ?1 " +
                    "WHERE h.id_espacio = ?2 " +
                    "ORDER BY h.id_horarios", nativeQuery = true)
    List<HorariosConsultaDTO> obtenerHorariosConsulta(Date fecha, Integer idEspacio);
}
