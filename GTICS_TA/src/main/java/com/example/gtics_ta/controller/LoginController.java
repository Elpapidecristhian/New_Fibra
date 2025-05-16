package com.example.gtics_ta.controller;

import com.example.gtics_ta.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String mostrarLogin(){
        return"main/Login/login";
    }

    @GetMapping("/signup")
    public String registrarse(){
        return"main/Login/signup";
    }

    @GetMapping("/recoverpass")
    public String recuperarContrasenia(){
        return"main/Login/recoverpass";
    }

}
