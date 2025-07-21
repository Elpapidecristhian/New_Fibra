package com.example.gtics_ta.Repository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.gtics_ta.Entity.Usuario;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    List<Usuario> findByActivo(boolean isActivo);
    long count(); // total de usuarios
    long countByActivo(boolean activo);
    List<Usuario> findAll();
    Usuario findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    boolean existsByDni(Integer dni);

    // Buscar usuarios por rol
    List<Usuario> findByRol_IdRol(Integer idRol);

    //Buscar por nombre de rol y si está activo
    List<Usuario> findByRol_NombreAndActivo(String nombre, boolean activo);
}
