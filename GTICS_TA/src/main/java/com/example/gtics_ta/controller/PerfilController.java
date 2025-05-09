package com.example.gtics_ta.controller;

import com.example.gtics_ta.entity.Usuario;
import com.example.gtics_ta.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/coordinador")
public class PerfilController {
    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping("/perfil")
    public String perfilUsuario(Model model) {
        Usuario usuario = usuarioRepository.findById(11).orElse(null);

        if (usuario == null) {
            return "redirect:/error";
        }

        model.addAttribute("usuario", usuario);
        return "main/coordinador/perfil";
    }

   // @PostMapping
    //public String subirFoto(@RequestParam("fotoPerfil") MultipartFile fotoPerfil, RedirectAttributes redirectAttributes) {

}
