package com.example.gtics_ta.Controllers;

import com.example.gtics_ta.Entity.Usuario;
import com.example.gtics_ta.Repository.UsuarioRepository;
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
import java.util.Optional;

@Controller
@RequestMapping("/coordinador")
public class CoordinadorController {
    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping("/perfil/{id}")
    public String perfilUsuario(@PathVariable Integer id, Model model) {
        Optional<Usuario> usuarioopt = usuarioRepository.findById(id);
        if (usuarioopt.isPresent()) {
            Usuario usuario = usuarioopt.get();
            model.addAttribute("usuario", usuario);
            return "coordinador/perfil";

        }else{
            return "redirect:/error";
        }
    }


    @GetMapping("/principal")
    public String mostrarPaginaPrincipal(Model model) {
        return "coordinador/principal";
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
