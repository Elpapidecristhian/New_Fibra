package com.example.gtics_ta.controller;

import org.springframework.ui.Model;
import com.example.gtics_ta.Entity.Reservas;
import com.example.gtics_ta.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/misReservas")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    //Busquéda por nombre de espacios
    @GetMapping("")
    public String listarReservas(@RequestParam(value = "nombre", required = false) String nombre, Model model) {
        List<Reservas> reservas= (nombre == null || nombre.isEmpty()) ?
                reservaRepository.findAll() :
                reservaRepository.findByIdEspacioNombreContainingIgnoreCase(nombre);
        model.addAttribute("listaReservas", reservas);
        return "vecino_reservas";
    }

    @GetMapping("/detalles/{id}")
    public String verReserva(@PathVariable("id") Integer id, Model model) {
        Reservas reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + id));
        model.addAttribute("reserva", reserva);
        return "vecino_reserva_detalles";
    }
}
