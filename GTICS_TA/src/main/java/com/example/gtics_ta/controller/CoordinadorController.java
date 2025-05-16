package com.example.gtics_ta.controller;

import com.example.gtics_ta.Entity.Asistencia;
import com.example.gtics_ta.Entity.Usuario;
import com.example.gtics_ta.dto.UsuarioDTO;
import com.example.gtics_ta.repository.AsistenciaRepository;
import com.example.gtics_ta.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

@Controller
@RequestMapping("/coordinador")
public class CoordinadorController {
    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping("/perfil/{id}")
    public String perfilUsuario(@PathVariable Integer id, Model model) {
        UsuarioDTO usuarioDTO = usuarioRepository.findUsuarioDTOById(id);
        if (usuarioDTO == null) {
            return "redirect:/error";
        }
        model.addAttribute("usuario", usuarioDTO);
        return "main/coordinador/perfil";
    }


    @GetMapping("/principal")
    public String mostrarPaginaPrincipal(Model model) {
        return "main/coordinador/principal";
    }

    @GetMapping("/foto/{id}")
    public ResponseEntity<byte[]> mostrarFoto(@PathVariable Integer id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario != null && usuario.getFoto() != null) {
            byte[] foto = usuario.getFoto();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // O ajusta según tu formato
            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/actualizar/{id}")
    public String actualizarPerfilUsuario(@PathVariable Integer id,
                                @RequestParam("celular") String celular,
                                @RequestParam("foto") MultipartFile foto) {

        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario != null) {
            usuario.setNumCelular(Integer.parseInt(celular));
            try {
                if (!foto.isEmpty()) {
                    usuario.setFoto(foto.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            usuarioRepository.save(usuario);
        }
        return "redirect:/coordinador/perfil/" + id;
    }

}
