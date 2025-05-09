package com.example.gtics_ta.repository;

import com.example.gtics_ta.entity.Fotos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotosRepository extends JpaRepository<Fotos, Integer> {
}
