package com.example.gtics_ta.repositories;

import com.example.gtics_ta.Entity.EspaciosDeportivos;
import com.example.gtics_ta.Entity.Horarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Optional;


@Repository
public interface HorariosRepository extends JpaRepository<Horarios, Integer> {
    // Encontrar un horario específico por horas y espacio
    Optional<Horarios> findByHoraInicioAndHoraFinAndEspacio(LocalTime horaInicio, LocalTime horaFin, EspaciosDeportivos espacio);

}
