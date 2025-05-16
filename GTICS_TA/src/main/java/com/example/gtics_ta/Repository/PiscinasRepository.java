package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.Piscinas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PiscinasRepository extends JpaRepository<Piscinas, Integer> {
    Piscinas findByIdEspacio(int idEspacio);
}
