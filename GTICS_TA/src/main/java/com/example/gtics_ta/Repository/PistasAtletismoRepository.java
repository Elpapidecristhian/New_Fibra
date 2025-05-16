package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.PistasAtletismo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PistasAtletismoRepository extends JpaRepository<PistasAtletismo, Integer> {
    PistasAtletismo findByIdEspacio(int idEspacio);
}
