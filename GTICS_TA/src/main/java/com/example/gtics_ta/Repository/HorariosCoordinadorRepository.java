package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.HorariosCoordinador;
import com.example.gtics_ta.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HorariosCoordinadorRepository extends JpaRepository<HorariosCoordinador, Integer> {

    // Buscar horarios de un coordinador para una fecha específica
    @Query("SELECT h FROM HorariosCoordinador h WHERE h.usuario = :usuario AND :fecha BETWEEN h.fechaInicio AND h.fechaFin")
    List<HorariosCoordinador> findByUsuarioAndFecha(@Param("usuario") Usuario usuario, @Param("fecha") Date fecha);

    // Buscar horarios por usuario
    List<HorariosCoordinador> findByUsuario(Usuario usuario);

    // Buscar horarios por rango de fechas
    @Query("SELECT h FROM HorariosCoordinador h WHERE h.fechaInicio <= :fechaFin AND h.fechaFin >= :fechaInicio")
    List<HorariosCoordinador> findByFechaRange(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);
}
