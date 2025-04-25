package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.Horarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorariosRepository extends JpaRepository<Horarios, Integer> {

    List<Horarios> findByIdEspacio(Integer idEspacio);
}
