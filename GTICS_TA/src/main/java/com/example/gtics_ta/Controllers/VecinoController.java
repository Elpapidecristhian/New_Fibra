package com.example.gtics_ta.Controllers;

import com.example.gtics_ta.Entity.EspaciosDeportivos;
import com.example.gtics_ta.Entity.Usuario;
import com.example.gtics_ta.Repository.EspaciosDeportivosRepository;
import com.example.gtics_ta.Repository.HorariosRepository;
import com.example.gtics_ta.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/vecino")
public class VecinoController {
    @Autowired
    EspaciosDeportivosRepository espaciosDeportivosRepository;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    HorariosRepository horariosRepository;

    @GetMapping(value = {"","/"})
    public String index(Model model) {
        return "vecino/index";
    }

    @GetMapping("/reservar")
    public String reservar(Model model, int idUsuario, int idEspacio) {

        Optional<Usuario> optusuario = usuarioRepository.findById(idUsuario);
        Optional<EspaciosDeportivos> optespacio = espaciosDeportivosRepository.findById(idEspacio);

        if(optusuario.isPresent() && optespacio.isPresent()) {
            Usuario usuario = optusuario.get();
            EspaciosDeportivos espacio = optespacio.get();
            model.addAttribute("usuario", usuario);
            model.addAttribute("espacio", espacio);
        }
        return "vecino/reservar";
    }
}
