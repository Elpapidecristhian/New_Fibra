package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.EspaciosDeportivos;
import com.example.gtics_ta.Entity.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Integer> {

    // Buscar mantenimientos por espacio deportivo
    List<Mantenimiento> findByEspacioOrderByFechaInicioDesc(EspaciosDeportivos espacio);

    // Buscar mantenimientos por estado
    List<Mantenimiento> findByEstadoMantenimientoOrderByFechaInicioDesc(Mantenimiento.EstadoMantenimiento estado);

    // Buscar mantenimientos programados para una fecha específica
    @Query("SELECT m FROM Mantenimiento m WHERE m.fechaInicio <= :fecha AND m.fechaFin >= :fecha")
    List<Mantenimiento> findMantenimientosEnFecha(@Param("fecha") LocalDate fecha);

    // Buscar mantenimientos que requieren cierre total en una fecha
    @Query("SELECT m FROM Mantenimiento m WHERE m.fechaInicio <= :fecha AND m.fechaFin >= :fecha AND m.requiereCierreTotal = true")
    List<Mantenimiento> findMantenimientosConCierreTotalEnFecha(@Param("fecha") LocalDate fecha);

    // Buscar mantenimientos por espacio y rango de fechas
    @Query("SELECT m FROM Mantenimiento m WHERE m.espacio = :espacio AND " +
           "((m.fechaInicio BETWEEN :fechaInicio AND :fechaFin) OR " +
           "(m.fechaFin BETWEEN :fechaInicio AND :fechaFin) OR " +
           "(m.fechaInicio <= :fechaInicio AND m.fechaFin >= :fechaFin))")
    List<Mantenimiento> findMantenimientosPorEspacioYRangoFechas(
        @Param("espacio") EspaciosDeportivos espacio,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );

    // Buscar mantenimientos activos (programados o en progreso)
    @Query("SELECT m FROM Mantenimiento m WHERE m.estadoMantenimiento IN ('PROGRAMADO', 'EN_PROGRESO') ORDER BY m.fechaInicio ASC")
    List<Mantenimiento> findMantenimientosActivos();

    // Contar mantenimientos por tipo
    @Query("SELECT COUNT(m) FROM Mantenimiento m WHERE m.tipoMantenimiento = :tipo")
    Long countByTipoMantenimiento(@Param("tipo") Mantenimiento.TipoMantenimiento tipo);
}
