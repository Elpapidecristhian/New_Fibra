package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.Reservas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservasRepository extends JpaRepository<Reservas, Integer> {
    List<Reservas> findByEspacioDeportivo_NombreContainingIgnoreCase(String nombre);

    // Filtrar por tipo de espacio
    List<Reservas> findByEspacioDeportivo_TipoEspacio_Id(Integer tipoEspacioId);

    // Filtrar por tipo de espacio y nombre
    List<Reservas> findByEspacioDeportivo_TipoEspacio_IdAndEspacioDeportivo_NombreContainingIgnoreCase(Integer tipoEspacioId, String nombre);

    // Buscar reservas por ID de pago
    List<Reservas> findByPago_Id(Integer pagoId);
}
