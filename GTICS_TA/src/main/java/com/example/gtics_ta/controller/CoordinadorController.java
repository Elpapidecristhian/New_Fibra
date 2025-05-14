package com.example.gtics_ta.controller;

import com.example.gtics_ta.Entity.Usuario;
import com.example.gtics_ta.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/coordinador")
public class CoordinadorController {
    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping("/perfil")
    public String perfilUsuario(Model model) {
        Usuario usuario = usuarioRepository.findById(1).orElse(null);

        if (usuario == null) {
            return "redirect:/error";
        }

        model.addAttribute("usuario", usuario);
        return "main/coordinador/perfil";
    }

    @GetMapping("/principal")
    public String mostrarPaginaPrincipal() {
        return "main/coordinador/principal";
    }

   // @PostMapping
    //public String subirFoto(@RequestParam("fotoPerfil") MultipartFile fotoPerfil, RedirectAttributes redirectAttributes) {

}
