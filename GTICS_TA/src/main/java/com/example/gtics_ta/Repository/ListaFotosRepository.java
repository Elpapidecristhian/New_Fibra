package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.ListaFotos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListaFotosRepository extends JpaRepository<ListaFotos, Integer> {

}
