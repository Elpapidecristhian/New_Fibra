package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.Suscripciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuscripcionesRepository extends JpaRepository<Suscripciones, Integer> {
    List<Suscripciones> findByUsuarioId(Integer id);
}