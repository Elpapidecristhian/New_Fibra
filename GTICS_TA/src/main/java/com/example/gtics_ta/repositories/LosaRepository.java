package com.example.gtics_ta.repositories;

import com.example.gtics_ta.Entity.Losa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LosaRepository extends JpaRepository<Losa, Integer> {
    // Puedes agregar métodos personalizados si quieres
}
