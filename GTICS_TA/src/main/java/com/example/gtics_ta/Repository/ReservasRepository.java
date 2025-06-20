package com.example.gtics_ta.Repository;

import com.example.gtics_ta.DTO.ResumenDTO;
import com.example.gtics_ta.Entity.Reservas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservasRepository extends JpaRepository<Reservas, Integer> {

    /* Métodos de consulta derivados */
    List<Reservas> findByEspacioDeportivo_NombreContainingIgnoreCase(String nombre);
    List<Reservas> findByEspacioDeportivo_TipoEspacio_Id(Integer tipoEspacioId);
    List<Reservas> findByEspacioDeportivo_TipoEspacio_IdAndEspacioDeportivo_NombreContainingIgnoreCase(Integer tipoEspacioId, String nombre);
    List<Reservas> findByPago_Id(Integer pagoId);
    List<Reservas> findByFechaReserva(LocalDate fechaReserva);
    List<Reservas> findByFechaReservaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    List<Reservas> findByEstadoReserva(Reservas.EstadoReserva estadoReserva);

    /* Métodos de consulta personalizados */
    @Query("SELECT r FROM Reservas r WHERE r.fechaReserva BETWEEN :fechaInicio AND :fechaFin AND r.estadoReserva = 'ACTIVA' ORDER BY r.fechaReserva, r.horario.horaInicio")
    List<Reservas> findReservasActivasEnRango(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT r FROM Reservas r WHERE r.estadoReserva = 'ACTIVA' AND r.fechaReserva >= CURRENT_DATE")
    List<Reservas> findReservasSinCoordinador();

    /* Métodos de reportes y estadísticas */
    @Query(value = """
        SELECT SUM(p.cantidad), COUNT(r.id_reservas)
        FROM reservas r
        JOIN pagos p ON r.id_pagos = p.id_pagos
        WHERE r.fecha_reserva >= DATE_SUB(CURDATE(), INTERVAL 90 DAY)
          AND r.fecha_reserva <= CURDATE()""", nativeQuery = true)
    List<Object[]> resumenUltimosTresMesesRaw();

    @Query("SELECT new com.example.gtics_ta.DTO.ResumenDTO(COALESCE(SUM(p.cantidad), 0), COUNT(r)) " +
            "FROM Reservas r JOIN r.pago p WHERE YEAR(r.fechaReserva) = YEAR(CURRENT_DATE)")
    ResumenDTO resumenAnual();

    @Query(value = """
        SELECT MONTH(r.fecha_reserva) AS mes, SUM(p.cantidad) AS total, COUNT(*) AS reservas
        FROM reservas r
        JOIN pagos p ON r.id_pagos = p.id_pagos
        WHERE r.fecha_reserva >= DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 2 MONTH), '%Y-%m-01')
          AND r.fecha_reserva < DATE_ADD(LAST_DAY(CURDATE()), INTERVAL 1 DAY)
        GROUP BY MONTH(r.fecha_reserva)
        ORDER BY mes""", nativeQuery = true)
    List<Object[]> reporteMensualUltimos3Meses();

    @Query(value = """
        SELECT MONTH(r.fecha_reserva) AS mes, SUM(p.cantidad) AS total, COUNT(*) AS reservas
        FROM reservas r
        JOIN pagos p ON r.id_pagos = p.id_pagos
        WHERE YEAR(r.fecha_reserva) = YEAR(CURDATE())
        GROUP BY MONTH(r.fecha_reserva)
        ORDER BY MONTH(r.fecha_reserva)""", nativeQuery = true)
    List<Object[]> reporteMensualAnual();

    @Query(value = """
        SELECT ed.nombre, COUNT(*) as cantidad
        FROM reservas r
        JOIN espaciosdeportivos ed ON r.id_espacio = ed.id_espacio
        GROUP BY ed.nombre
        ORDER BY cantidad DESC
        LIMIT 10""", nativeQuery = true)
    List<Object[]> top10ServiciosMasReservados();

    @Query(value = """
        SELECT ed.nombre, COUNT(*) as cantidad
        FROM reservas r
        JOIN espaciosdeportivos ed ON r.id_espacio = ed.id_espacio
        GROUP BY ed.nombre""", nativeQuery = true)
    List<Object[]> porcentajeReservasPorServicio();

    @Query("SELECT COUNT(r) FROM Reservas r")
    Long contarTotalReservas();

    @Query("SELECT COUNT(r) FROM Reservas r WHERE DATE(r.fechaReserva) = CURRENT_DATE")
    Long contarReservasHoy();

    @Query("SELECT u.id, u.nombres, u.apellidos, COUNT(r) " +
            "FROM Reservas r " +
            "JOIN r.usuario u " +
            "GROUP BY u.id, u.nombres, u.apellidos " +
            "ORDER BY COUNT(r) DESC")
    List<Object[]> top10UsuariosConMasReservas();

    @Query("SELECT HOUR(h.horaInicio), COUNT(r) " +
            "FROM Reservas r " +
            "JOIN r.horario h " +
            "GROUP BY HOUR(h.horaInicio) " +
            "ORDER BY HOUR(h.horaInicio)")
    List<Object[]> distribucionReservasPorHora();
    @Query("SELECT r FROM Reservas r WHERE r.espacioDeportivo.id = :id")
    List<Reservas> findByEspacioDeportivoId(@Param("id") Integer id);
}