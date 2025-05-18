package com.example.gtics_ta.repositories;

import com.example.gtics_ta.Entity.Horarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HorariosRepository extends JpaRepository<Horarios, Integer> {
    // Aquí puedes añadir consultas personalizadas si es necesario, por ejemplo:
    // List<Horarios> findByEspacio(EspaciosDeportivos espacio);
}
