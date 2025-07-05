package com.example.gtics_ta.Controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Controller
public class MainController {

    @GetMapping("/")
    public String redireccionPorRol(Authentication authentication) {
        if (authentication == null) {
            // No autenticado: redirige a login
            return "redirect:/login";
        }

        // Obtener los roles del usuario
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        if (authorities.stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase("vecino"))) {
            return "redirect:/vecino";
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("admin"))) {
            return "redirect:/admin/";
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("coordinador"))) {
            return "redirect:/coordinador/principal";
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("superadmin"))) {
            return "redirect:/Superadmin";
        }

        // Si no tiene rol conocido, redirige a una página de acceso denegado
        return "redirect:/login";
    }
}