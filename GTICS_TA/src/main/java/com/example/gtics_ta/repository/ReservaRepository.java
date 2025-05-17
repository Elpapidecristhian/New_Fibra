package com.example.gtics_ta.repository;

import com.example.gtics_ta.Entity.Reservas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository <Reservas, Integer> {
    List<Reservas> findByEspacioDeportivoNombreContainingIgnoreCase(String nombre);
}
