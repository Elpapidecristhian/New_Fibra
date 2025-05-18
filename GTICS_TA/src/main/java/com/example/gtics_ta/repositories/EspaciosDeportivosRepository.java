package com.example.gtics_ta.repositories;

import com.example.gtics_ta.Entity.EspaciosDeportivos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspaciosDeportivosRepository extends JpaRepository<EspaciosDeportivos, Integer> {
    // No es necesario volver a declarar findAll(), JpaRepository ya lo tiene
}

