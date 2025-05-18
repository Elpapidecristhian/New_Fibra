package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.Fotos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FotosRepository extends JpaRepository<Fotos, Integer> {
    List<Fotos> findByIdListaFotos(Integer id);
}
