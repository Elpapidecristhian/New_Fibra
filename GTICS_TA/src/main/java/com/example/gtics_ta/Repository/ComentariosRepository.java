package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.Comentarios;
import com.example.gtics_ta.Entity.EspaciosDeportivos;
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
    
    // Buscar comentarios por tipo usando ENUM
    List<Comentarios> findByTipoComentarioOrderByFechaCreacionDesc(Comentarios.TipoComentario tipoComentario);

    // Buscar todos los comentarios ordenados por fecha
    List<Comentarios> findAllByOrderByFechaCreacionDesc();

    // Buscar comentarios de reparación/mantenimiento
    @Query("SELECT c FROM Comentarios c WHERE c.tipoComentario = 'REPARACION' ORDER BY c.fechaCreacion DESC")
    List<Comentarios> findMantenimientos();

    // Buscar comentarios regulares
    @Query("SELECT c FROM Comentarios c WHERE c.tipoComentario = 'COMENTARIO' ORDER BY c.fechaCreacion DESC")
    List<Comentarios> findComentariosRegulares();

    // Buscar comentarios por espacio y tipo
    @Query("SELECT c FROM Comentarios c WHERE c.espacio.id = :espacioId AND c.tipoComentario = :tipo ORDER BY c.fechaCreacion DESC")
    List<Comentarios> findByEspacioAndTipo(@Param("espacioId") Integer espacioId, @Param("tipo") Comentarios.TipoComentario tipo);

    // Contar comentarios por tipo
    @Query("SELECT COUNT(c) FROM Comentarios c WHERE c.tipoComentario = :tipo")
    Long countByTipoComentario(@Param("tipo") Comentarios.TipoComentario tipo);

    // Buscar comentarios que requieren mantenimiento
    @Query("SELECT c FROM Comentarios c WHERE c.requiereMantenimiento = true AND c.revisadoPorAdmin = false ORDER BY c.fechaCreacion DESC")
    List<Comentarios> findComentariosQueRequierenMantenimiento();

    // Buscar comentarios no revisados por admin
    @Query("SELECT c FROM Comentarios c WHERE c.revisadoPorAdmin = false ORDER BY c.fechaCreacion DESC")
    List<Comentarios> findComentariosNoRevisados();
}
