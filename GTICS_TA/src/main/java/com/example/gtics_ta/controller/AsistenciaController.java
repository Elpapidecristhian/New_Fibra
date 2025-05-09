package com.example.gtics_ta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AsistenciaController {

    @GetMapping("/principal")
    public String mostrarPaginaPrincipal() {
        return "main/coordinador/principal";
    }
}
