package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.Reservas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservasRepository extends JpaRepository<Reservas, Integer> {
    List<Reservas> findByEspacioDeportivo_NombreContainingIgnoreCase(String nombre);
}
