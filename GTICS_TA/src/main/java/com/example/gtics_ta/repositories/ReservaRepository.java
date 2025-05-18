package com.example.gtics_ta.repositories;

import com.example.gtics_ta.Entity.Reservas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reservas, Integer> {
    // Aquí puedes añadir consultas personalizadas si es necesario, por ejemplo:
    // List<Reservas> findByUsuario(Usuario usuario);
}

