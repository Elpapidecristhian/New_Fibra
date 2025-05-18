package com.example.gtics_ta.Controllers;

import com.example.gtics_ta.DTO.HorariosConsultaDTO;
import com.example.gtics_ta.Entity.*;
import com.example.gtics_ta.Repository.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    @Autowired
    PiscinasRepository piscinasRepository;
    @Autowired
    CanchasFutbolRepository canchasFutbolRepository;
    @Autowired
    PistasAtletismoRepository pistasAtletismoRepository;
    @Autowired
    private EstadiosRepository estadiosRepository;
    @Autowired
    private FotosRepository fotosRepository;

    @GetMapping(value = {"","/"})
    public String listaEspacios(@RequestParam(name = "tipo", required = false) Integer id,
                                @RequestParam(name = "fecha", required = false) String fecha,
                                @RequestParam(name = "nombre", required = false) String nombre,
                                Model model
    ) {
        List<EspaciosDeportivos> espacios;

        if (id != null) {
            if(nombre != null && !nombre.isEmpty()) {
                espacios = espaciosDeportivosRepository.findByTipoEspacio_IdAndNombreContaining(id, nombre);
            }else {
                espacios = espaciosDeportivosRepository.findByTipoEspacio_Id(id);
            }
        } else {
            if(nombre != null && !nombre.isEmpty()) {
                espacios = espaciosDeportivosRepository.findByNombreContaining(nombre);
            }
            else {
                espacios = espaciosDeportivosRepository.findAll();
            }
        }
        if(fecha != null && fecha.isEmpty()){fecha = LocalDate.now().format(DateTimeFormatter.ISO_DATE);}
        if(fecha == null){fecha = LocalDate.now().format(DateTimeFormatter.ISO_DATE);}
        String hoy = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        model.addAttribute("minDate", hoy);
        model.addAttribute("espacios", espacios);
        model.addAttribute("tipoSeleccionado", id);
        model.addAttribute("fechaSeleccionada", fecha);
        model.addAttribute("nombreSeleccionado", nombre);

        return "vecino/espacios";
    }

    @GetMapping("/detalles")
    public String espacioDetalles(Model model, @RequestParam(name = "idEspacio") int id, @RequestParam(name = "fecha") String fecha){
        Optional<EspaciosDeportivos> optEspacio = espaciosDeportivosRepository.findById(id);
        Piscinas piscina;
        CanchasFutbol canchaFutbol;
        PistasAtletismo pista;
        Estadios estadio;

        if(optEspacio.isPresent()) {
            EspaciosDeportivos espacio = optEspacio.get();
            model.addAttribute("espacio", espacio);
            if(espacio.getTipoEspacio().getId() == 1){
                piscina = piscinasRepository.findByIdEspacio(espacio.getId());
                model.addAttribute("piscina", piscina);
            } else if (espacio.getTipoEspacio().getId() == 2) {
                canchaFutbol = canchasFutbolRepository.findByIdEspacio(espacio.getId());
                model.addAttribute("canchaFutbol", canchaFutbol);
            } else if (espacio.getTipoEspacio().getId() == 3) {
                pista = pistasAtletismoRepository.findByIdEspacio(espacio.getId());
                model.addAttribute("pista", pista);
            } else if (espacio.getTipoEspacio().getId() == 4) {
                estadio = estadiosRepository.findByIdEspacio(espacio.getId());
                model.addAttribute("estadio", estadio);
            }
            model.addAttribute("fecha", fecha);
        }
        return "vecino/detalles";
    }

    @GetMapping("/reservar")
    public String reservar(Model model, @ModelAttribute("reserva") Reservas reservas, HttpSession session, @RequestParam(name = "idEspacio") int idEspacio, @RequestParam(name = "fecha") String fecha) throws ParseException {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaconv = format.parse(fecha);

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Optional<EspaciosDeportivos> optespacio = espaciosDeportivosRepository.findById(idEspacio);

        if( optespacio.isPresent()) {
            reservas = new Reservas();
            EspaciosDeportivos espacio = optespacio.get();
            List<HorariosConsultaDTO> listaHorarios = horariosRepository.obtenerHorariosConsulta(fechaconv, espacio.getId());
            reservas.setUsuario(usuario);
            reservas.setEspacioDeportivo(espacio);
            reservas.setFechaReserva(fechaconv);
            model.addAttribute("reserva", reservas);
            model.addAttribute("listaHorarios", listaHorarios);
            String hoy = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            model.addAttribute("minDate", hoy);
        }
        return "vecino/reservar";
    }

    @PostMapping("/guardarreserva")
    public String guardarreserva(@ModelAttribute("reserva") Reservas reserva) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        reserva.setFechaRegistro(timestamp);

        //Creacion del Horario Reservado
        HorarioReservado horarioReservado = new HorarioReservado();
        horarioReservado.setFecha(reserva.getFechaReserva());
        horarioReservado.setHorario(reserva.getHorario());

        //Pago chancado
        Pagos pago = new Pagos();
        pago.setId(1);
        reserva.setPago(pago);

        horarioReservadoRepository.save(horarioReservado);
        reservasRepository.save(reserva);
        return "redirect:vecino/";
    }

    @GetMapping("/reservas")
    public String listarReservas(@RequestParam(value = "nombre", required = false) String nombre, Model model) {
        List<Reservas> reservas= (nombre == null || nombre.isEmpty()) ?
                reservasRepository.findAll() :
                reservasRepository.findByEspacioDeportivo_NombreContainingIgnoreCase(nombre);
        model.addAttribute("listaReservas", reservas);
        return "vecino/reservas";
    }

    @GetMapping("/perfil")
    public String vecinoPerfil(@ModelAttribute("usuario") Usuario usuario, HttpSession session, Model model) {
        usuario = (Usuario) session.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        return "vecino/perfil";
    }

    @PostMapping("/guardarperfil")
    public String guardarPerfil(@ModelAttribute("usuario") @Valid Usuario usuario, BindingResult bindingResult, @RequestParam("archivo") MultipartFile file , Model model) {
        if(bindingResult.hasErrors()) {
            return "vecino/perfil";
        }

        if(file.isEmpty()) {
            return "vecino/perfil";
        }

        String fileName = file.getOriginalFilename();

        if (fileName.contains("..")){
            model.addAttribute("msg","Debe ingresar un archivo válido");
            return "vecino/perfil";
        }

        try {
            usuario.setFoto(file.getBytes());
            usuario.setFotoNombre(fileName);
            usuario.setFotoTipoArchivo(file.getContentType());
            usuarioRepository.save(usuario);
            return "redirect:/vecino/perfil";
        } catch (Exception e) {
            e.printStackTrace();
            return "vecino/perfil";
        }
    }

    @GetMapping("/profileimage/{id}")
    public ResponseEntity<byte[]> mostrarImagenPefil(@PathVariable("id") Integer id) {
        Optional<Usuario> optusuario = usuarioRepository.findById(id);
        if(optusuario.isPresent()) {
            Usuario usuario = optusuario.get();

            byte[] image = usuario.getFoto();
            if (image == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            String tipoArchivo = usuario.getFotoTipoArchivo();
            if (tipoArchivo == null || tipoArchivo.isBlank()) {
                tipoArchivo = "application/octet-stream"; // tipo MIME por defecto
            }

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(tipoArchivo));

            return new ResponseEntity<>(
                    image,
                    httpHeaders,
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
