package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    // No necesitas métodos adicionales para listar todos los roles

}
