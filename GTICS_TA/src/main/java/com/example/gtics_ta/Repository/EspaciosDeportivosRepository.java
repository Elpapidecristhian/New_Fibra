package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.EspaciosDeportivos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspaciosDeportivosRepository extends JpaRepository<EspaciosDeportivos, Integer> {
    List<EspaciosDeportivos> findAll();
}
