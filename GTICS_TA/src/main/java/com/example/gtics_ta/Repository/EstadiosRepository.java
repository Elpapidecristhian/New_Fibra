package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.Estadios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadiosRepository extends JpaRepository<Estadios, Integer> {
    Estadios findByIdEspacio(int id);
}
