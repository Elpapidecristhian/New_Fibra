package com.example.gtics_ta.Controllers;

import com.example.gtics_ta.DTO.ReservasDTO;
import com.example.gtics_ta.Entity.*;
import com.example.gtics_ta.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
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
    public String listaEspacios(@RequestParam(name = "tipo", required = false) String id,
                                @RequestParam(name = "fecha", required = false) String fecha,
                                Model model
    ) {
        List<EspaciosDeportivos> espacios;

        if (id != null) {
            espacios = espaciosDeportivosRepository.findByTipoEspacio_Id(Integer.parseInt(id));
        } else {
            espacios = espaciosDeportivosRepository.findAll();
        }

        model.addAttribute("espacios", espacios);
        model.addAttribute("tipoSeleccionado", id);
        model.addAttribute("fechaSeleccionada", fecha);

        return "vecino/espacios";  // nombre del HTML
    }

    @GetMapping("/detalles")
    public String espacioDetalles(Model model, @RequestParam(name = "idEspacio") int id) {
        Optional<EspaciosDeportivos> optEspacio = espaciosDeportivosRepository.findById(id);

        if(optEspacio.isPresent()) {
            EspaciosDeportivos espacio = optEspacio.get();
            model.addAttribute("espacio", espacio);
        }
        return "vecino/detalles";
    }

    @GetMapping("/reservar")
    public String reservar(Model model, @RequestParam(name = "idUsuario") int idUsuario, @RequestParam(name = "idEspacio") int idEspacio, @RequestParam(name = "fecha") String fecha) throws ParseException {

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
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        reserva.setFechaRegistro(timestamp);

        Pagos pago = new Pagos();
        pago.setId(1);
        reserva.setPago(pago);


        horarioReservadoRepository.save(horarioReservado);
        reservasRepository.save(reserva);
        return "redirect:/vecino/";
    }

    @GetMapping("/reservas")
    public String listarReservas(@RequestParam(value = "nombre", required = false) String nombre, Model model) {
        List<Reservas> reservas= (nombre == null || nombre.isEmpty()) ?
                reservasRepository.findAll() :
                reservasRepository.findByEspacioDeportivo_NombreContainingIgnoreCase(nombre);
        model.addAttribute("listaReservas", reservas);
        return "vecino/reservas";
    }

    @GetMapping("/reservas/detalles/{id}")
    public String verReserva(@PathVariable("id") Integer id, Model model) {
        Reservas reserva = reservasRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + id));
        model.addAttribute("reserva", reserva);
        return "vecino_reserva_detalles";
    }
}
