package com.example.gtics_ta.repository;

import com.example.gtics_ta.Entity.Usuario;
import com.example.gtics_ta.dto.UsuarioDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    @Query("SELECT new com.example.gtics_ta.dto.UsuarioDTO(u.id, u.nombres, u.apellidos, u.username, u.direccion, u.dni, u.numCelular, u.foto, u.fechaNacimiento) FROM Usuario u WHERE u.id = :id")
    UsuarioDTO findUsuarioDTOById(@Param("id") Integer id);

}