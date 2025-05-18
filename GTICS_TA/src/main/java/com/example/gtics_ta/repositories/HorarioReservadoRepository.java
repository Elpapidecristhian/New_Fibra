package com.example.gtics_ta.repositories;

import com.example.gtics_ta.Entity.HorarioReservado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;

public interface HorarioReservadoRepository extends JpaRepository<HorarioReservado, Integer> {

    List<HorarioReservado> findByHorario_IdHorariosInAndFechaAndEstadoIn(List<Integer> horariosIds, LocalDate fecha, List<String> estados);

    @Query("""
        SELECT hr FROM HorarioReservado hr
        WHERE hr.horario.espacio.id = :idEspacio
        AND hr.fecha = :fecha
        AND hr.estado IN :estados
        """)
    List<HorarioReservado> findByEspacioIdAndFechaAndEstados(int idEspacio, LocalDate fecha, List<String> estados);

    List<HorarioReservado> findByEstado(String estado);
}
