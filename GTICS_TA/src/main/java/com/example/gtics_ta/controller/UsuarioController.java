package com.example.gtics_ta.controller;

import com.example.gtics_ta.dto.UsuarioDTO;  // Importamos el DTO
import com.example.gtics_ta.Entity.Usuario;
import com.example.gtics_ta.repositories.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired

    private UsuarioRepository usuarioRepository;

    private Usuario obtenerUsuario(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @GetMapping("/{id}")
    public String viewUsuario(@PathVariable Integer id, Model model) {
        UsuarioDTO usuarioDTO = usuarioRepository.findUsuarioDTOById(id);  // Usamos el DTO en lugar de la entidad completa
        if (usuarioDTO == null) {
            return "error/usuario_no_encontrado";
        }
        model.addAttribute("usuario", usuarioDTO);  // Le pasamos el DTO a la vista
        return "main/usuario_perfil";  // Vista para mostrar el perfil
    }

    @PostMapping("/update/{id}")
    public String updateUsuario(@PathVariable Integer id,
                                @RequestParam("celular") String celular,
                                @RequestParam("foto") MultipartFile foto) {

        Usuario usuario = obtenerUsuario(id);
        if (usuario != null) {
            usuario.setNumCelular(Integer.parseInt(celular));
            try {
                if (!foto.isEmpty()) {
                    usuario.setFotoPerfil(foto.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace(); // Reemplazar con logging si deseas
            }
            usuarioRepository.save(usuario);
        }
        return "redirect:/usuario/" + id;
    }

    @GetMapping("/foto/{id}")
    public ResponseEntity<byte[]> mostrarFoto(@PathVariable Integer id) {
        Usuario usuario = obtenerUsuario(id);
        if (usuario != null && usuario.getFotoPerfil() != null) {
            byte[] foto = usuario.getFotoPerfil();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // O ajusta según tu formato
            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
