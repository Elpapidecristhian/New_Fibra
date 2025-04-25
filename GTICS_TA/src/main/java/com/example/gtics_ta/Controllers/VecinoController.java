package com.example.gtics_ta.Controllers;

import com.example.gtics_ta.DTO.ReservasDTO;
import com.example.gtics_ta.Entity.*;
import com.example.gtics_ta.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    @Autowired
    HorarioReservadoRepository horarioReservadoRepository;
    @Autowired
    ReservasRepository reservasRepository;

    @GetMapping(value = {"","/"})
    public String index(Model model) {
        return "vecino/index";
    }

    @GetMapping("/reservar")
    public String reservar(Model model, int idUsuario, int idEspacio, String fecha) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaconv = format.parse(fecha);
        Optional<Usuario> optusuario = usuarioRepository.findById(idUsuario);
        Optional<EspaciosDeportivos> optespacio = espaciosDeportivosRepository.findById(idEspacio);

        if(optusuario.isPresent() && optespacio.isPresent()) {
            Reservas reservas = new Reservas();
            Usuario usuario = optusuario.get();
            EspaciosDeportivos espacio = optespacio.get();
            List<Horarios> listaHorarios = horariosRepository.findByIdEspacio(espacio.getId());
            List<HorarioReservado> horarioReservados = new ArrayList<>();
            for(Horarios horario : listaHorarios) {
                HorarioReservado horarioreservado = horarioReservadoRepository.findByHorario_IdAndFecha(horario.getId(), fechaconv);
                horarioReservados.add(horarioreservado);
            }
            reservas.setUsuario(usuario);
            reservas.setEspacioDeportivo(espacio);
            reservas.setFechaReserva(fechaconv);
            model.addAttribute("reserva", reservas);
            model.addAttribute("listaHorarios", horarioReservados);
        }
        return "vecino/reservar";
    }

    @PostMapping("/guardarreserva")
    public String guardarreserva(Model model, Reservas reserva) {
        Usuario usuario = reserva.getUsuario();
        EspaciosDeportivos espacio = reserva.getEspacioDeportivo();
        HorarioReservado horarioReservado = reserva.getHorarioReservado();
        horarioReservado.setReservado(true);


        horarioReservadoRepository.save(horarioReservado);
        reservasRepository.save(reserva);
        return "redirect:/vecino/";
    }
}
