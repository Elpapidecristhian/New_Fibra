package com.example.gtics_ta.repositories;

import com.example.gtics_ta.Entity.Piscina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PiscinaRepository extends JpaRepository<Piscina, Integer> {
}

