package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.Comentarios;
import com.example.gtics_ta.Entity.EspaciosDeportivos;
import com.example.gtics_ta.Entity.TipoComentario;
import com.example.gtics_ta.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentariosRepository extends JpaRepository<Comentarios, Integer> {
    
    // Buscar comentarios por espacio deportivo
    List<Comentarios> findByEspacioOrderByFechaCreacionDesc(EspaciosDeportivos espacio);
    
    // Buscar comentarios por usuario
    List<Comentarios> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);
    
    // Buscar comentarios por tipo (1 = comentario, 2 = mantenimiento)
    List<Comentarios> findByTipoComentarioOrderByFechaCreacionDesc(TipoComentario tipoComentario);
    
    // Buscar comentarios por tipo de comentario ID
    List<Comentarios> findByTipoComentario_IdOrderByFechaCreacionDesc(Integer tipoComentarioId);
    
    // Buscar todos los comentarios ordenados por fecha
    List<Comentarios> findAllByOrderByFechaCreacionDesc();
    
    // Buscar comentarios de mantenimiento (id_tipo_comentario = 2)
    @Query("SELECT c FROM Comentarios c WHERE c.tipoComentario.id = 2 ORDER BY c.fechaCreacion DESC")
    List<Comentarios> findMantenimientos();
    
    // Buscar comentarios regulares (id_tipo_comentario = 1)
    @Query("SELECT c FROM Comentarios c WHERE c.tipoComentario.id = 1 ORDER BY c.fechaCreacion DESC")
    List<Comentarios> findComentariosRegulares();
    
    // Buscar comentarios por espacio y tipo
    @Query("SELECT c FROM Comentarios c WHERE c.espacio.id = :espacioId AND c.tipoComentario.id = :tipoId ORDER BY c.fechaCreacion DESC")
    List<Comentarios> findByEspacioAndTipo(@Param("espacioId") Integer espacioId, @Param("tipoId") Integer tipoId);
    
    // Contar comentarios por tipo
    @Query("SELECT COUNT(c) FROM Comentarios c WHERE c.tipoComentario.id = :tipoId")
    Long countByTipoComentario(@Param("tipoId") Integer tipoId);
}
