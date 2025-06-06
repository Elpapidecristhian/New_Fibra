package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.TipoComentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoComentarioRepository extends JpaRepository<TipoComentario, Integer> {
    
    // Buscar tipo de comentario por nombre
    TipoComentario findByNombre(String nombre);
    
    // Buscar todos los tipos ordenados por nombre
    List<TipoComentario> findAllByOrderByNombreAsc();
}
