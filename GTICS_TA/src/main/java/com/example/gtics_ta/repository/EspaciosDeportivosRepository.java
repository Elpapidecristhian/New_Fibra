package com.example.gtics_ta.repository;

import com.example.gtics_ta.entity.EspaciosDeportivos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EspaciosDeportivosRepository extends JpaRepository<EspaciosDeportivos, Integer> {
    List<EspaciosDeportivos> findByTipoEspacio_NombreTipo(String nombreTipo);

}
