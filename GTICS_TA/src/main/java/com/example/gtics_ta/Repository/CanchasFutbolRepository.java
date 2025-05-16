package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.CanchasFutbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CanchasFutbolRepository  extends JpaRepository<CanchasFutbol, Integer> {
    CanchasFutbol findByIdEspacio(int idEspacio);
}
