package com.example.gtics_ta.Controllers;

import com.example.gtics_ta.Entity.Usuario;
import com.example.gtics_ta.Repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    @GetMapping("/perfil")
    public String coordinadorPerfil(@ModelAttribute("usuario") Usuario usuario, @RequestParam(value = "id") Integer id, Model model) {
        Optional<Usuario> optuser = usuarioRepository.findById(id);
        if(optuser.isPresent()) {
            usuario = optuser.get();
            model.addAttribute("usuario", usuario);
        }
        return "coordinador/perfil";
    }

    @PostMapping("/guardarperfil")
    public String guardarPerfil(@ModelAttribute("usuario") @Valid Usuario usuario, BindingResult bindingResult, @RequestParam("archivo") MultipartFile file , Model model) {
        if(bindingResult.hasErrors()) {
            return "coordinador/perfil";
        }

        if(file.isEmpty()) {
            return "coordinador/perfil";
        }

        String fileName = file.getOriginalFilename();

        if (fileName.contains("..")){
            model.addAttribute("msg","Debe ingresar un archivo válido");
            return "coordinador/perfil";
        }

        try {
            usuario.setFoto(file.getBytes());
            usuario.setFotoNombre(fileName);
            usuario.setFotoTipoArchivo(file.getContentType());
            usuarioRepository.save(usuario);
            return "redirect:/coordinador/perfil?id=" + usuario.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return "coordinador/perfil";
        }
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> mostrarImagen(@PathVariable("id") Integer id) {
        Optional<Usuario> optusuario = usuarioRepository.findById(id);
        if(optusuario.isPresent()) {
            Usuario usuario = optusuario.get();

            byte[] image = usuario.getFoto();
            if (image == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            String tipoArchivo = usuario.getFotoTipoArchivo();
            if (tipoArchivo == null || tipoArchivo.isBlank()) {
                tipoArchivo = "application/octet-stream"; // tipo MIME por defecto
            }

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(
                    MediaType.parseMediaType(usuario.getFotoTipoArchivo()));

            return new ResponseEntity<>(
                    image,
                    httpHeaders,
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/principal")
    public String mostrarPaginaPrincipal(Model model) {
        return "coordinador/principal";
    }


}
