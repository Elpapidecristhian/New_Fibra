package com.example.gtics_ta.Controllers;

import com.example.gtics_ta.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String mostrarLogin(){
        return"login/login";
    }

    @GetMapping("/signup")
    public String registrarse(){
        return"login/signup";
    }

    @GetMapping("/recoverpass")
    public String recuperarContrasenia(){
        return"login/recoverpass";
    }

}
