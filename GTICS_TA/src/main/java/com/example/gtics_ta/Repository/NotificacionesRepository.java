package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.Notificaciones;
import com.example.gtics_ta.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionesRepository extends JpaRepository<Notificaciones, Integer> {

    // Buscar todas las notificaciones de un usuario ordenadas por fecha (más recientes primero)
    List<Notificaciones> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);

    // Contar cuántas notificaciones no leídas tiene un usuario
    int countByUsuarioAndLeidaFalse(Usuario usuario);
}
