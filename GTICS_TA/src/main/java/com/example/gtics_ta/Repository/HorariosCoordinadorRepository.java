package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.HorariosCoordinador;
import com.example.gtics_ta.Entity.Usuario;
import com.example.gtics_ta.Entity.EspaciosDeportivos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HorariosCoordinadorRepository extends JpaRepository<HorariosCoordinador, Integer> {

    // Buscar horarios de un coordinador específico
    List<HorariosCoordinador> findByUsuario(Usuario usuario);

    // Buscar horarios por rango de fechas
    List<HorariosCoordinador> findByFechaInicioBetween(Date fechaInicio, Date fechaFin);

    // Buscar horarios de un coordinador en un rango de fechas
    List<HorariosCoordinador> findByUsuarioAndFechaInicioBetween(Usuario usuario, Date fechaInicio, Date fechaFin);

    // Buscar horarios por espacio
    List<HorariosCoordinador> findByEspacio(EspaciosDeportivos espacio);

    // Buscar horarios de un coordinador en una fecha específica
    @Query("SELECT hc FROM HorariosCoordinador hc WHERE hc.usuario = :usuario AND :fecha BETWEEN hc.fechaInicio AND hc.fechaFin")
    List<HorariosCoordinador> findByUsuarioAndFecha(@Param("usuario") Usuario usuario, @Param("fecha") Date fecha);

    // Verificar si un coordinador tiene conflictos de horario
    @Query("SELECT hc FROM HorariosCoordinador hc WHERE hc.usuario = :usuario AND " +
           "((hc.fechaInicio <= :fechaFin AND hc.fechaFin >= :fechaInicio) OR " +
           "(hc.fechaInicio >= :fechaInicio AND hc.fechaInicio <= :fechaFin))")
    List<HorariosCoordinador> findConflictosHorario(@Param("usuario") Usuario usuario,
                                                   @Param("fechaInicio") Date fechaInicio,
                                                   @Param("fechaFin") Date fechaFin);

    // Obtener todos los coordinadores con horarios en una semana específica
    @Query("SELECT DISTINCT hc.usuario FROM HorariosCoordinador hc WHERE hc.fechaInicio <= :fechaFin AND hc.fechaFin >= :fechaInicio")
    List<Usuario> findCoordinadoresEnSemana(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);
}
