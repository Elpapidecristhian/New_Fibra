package com.example.gtics_ta.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.gtics_ta.Entity.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    List<Usuario> findByIsBaneado(boolean isBaneado);
    long count(); // total de usuarios

    long countByIsBaneado(boolean estado); // usuarios baneados
    Optional<Usuario> findByCorreo(String correo); // 👈 necesario para login

}
