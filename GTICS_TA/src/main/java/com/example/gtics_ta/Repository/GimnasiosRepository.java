package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.Gimnasios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GimnasiosRepository extends JpaRepository<Gimnasios, Integer> {
    Gimnasios findByIdEspacios(int id);
}