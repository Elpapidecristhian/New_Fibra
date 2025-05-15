package com.example.gtics_ta.repositories;

import com.example.gtics_ta.dto.UsuarioDTO;  // Importamos el DTO
import com.example.gtics_ta.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    @Query("SELECT new com.example.gtics_ta.dto.UsuarioDTO(u.idUsuario, u.nombres, u.apellidos, u.correo, u.direccion, u.dni, u.numCelular, u.fotoPerfil, u.fechaNacimiento) FROM Usuario u WHERE u.idUsuario = :id")
    UsuarioDTO findUsuarioDTOById(@Param("id") Integer id);

}
