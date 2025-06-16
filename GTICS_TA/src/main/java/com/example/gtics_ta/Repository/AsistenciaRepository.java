package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.Asistencia;
import com.example.gtics_ta.Entity.HorariosCoordinador;
import com.example.gtics_ta.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Integer> {

    // Buscar asistencia por horario coordinador y fecha
    Optional<Asistencia> findByHorariosCoordinadorAndFecha(HorariosCoordinador horariosCoordinador, Date fecha);

    // Buscar asistencias por coordinador
    List<Asistencia> findByHorariosCoordinador_Usuario(Usuario usuario);

    // Buscar asistencias por fecha
    List<Asistencia> findByFecha(Date fecha);

    // Buscar asistencias por rango de fechas
    @Query("SELECT a FROM Asistencia a WHERE a.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<Asistencia> findByFechaBetween(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);

    // Verificar si ya existe asistencia para un coordinador en una fecha específica
    boolean existsByHorariosCoordinadorAndFecha(HorariosCoordinador horariosCoordinador, Date fecha);
}