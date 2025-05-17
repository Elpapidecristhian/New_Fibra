package com.example.gtics_ta.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.gtics_ta.Entity.Usuario;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    List<Usuario> findByActivo(boolean isActivo);
    long count(); // total de usuarios

    List<Usuario> findAll();
    Usuario findByCorreo(String correo);

}
