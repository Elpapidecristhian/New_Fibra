package com.example.gtics_ta.Controllers;

import com.example.gtics_ta.DTO.ChatMessageDTO;
import com.example.gtics_ta.DTO.HorariosConsultaDTO;
import com.example.gtics_ta.Entity.*;
import com.example.gtics_ta.Repository.*;
import com.example.gtics_ta.Services.MailService;
import com.example.gtics_ta.Services.OpenAiService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/vecino")
public class VecinoController {

    @Autowired
    private OpenAiService openAiService;
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
    @Autowired
    private MailService emailService;

    @GetMapping(value = {"", "/"})
    public String vistaInicial(Model model) {
        return "redirect:/vecino/espacios";
    }

    //*********************************************************************************************
    //
    //                                    Lista de Espacios
    //
    //*********************************************************************************************

    @GetMapping("/espacios")
    public String listaEspacios(@RequestParam(name = "tipo", required = false) Integer id,
                                @RequestParam(name = "fecha", required = false) String fecha,
                                @RequestParam(name = "nombre", required = false) String nombre,
                                Model model
    ) {
        List<EspaciosDeportivos> espacios;

        if (id != null) {
            if (nombre != null && !nombre.isEmpty()) {
                espacios = espaciosDeportivosRepository.findByTipoEspacio_IdAndNombreContaining(id, nombre);
            } else {
                espacios = espaciosDeportivosRepository.findByTipoEspacio_Id(id);
            }
        } else {
            if (nombre != null && !nombre.isEmpty()) {
                espacios = espaciosDeportivosRepository.findByNombreContaining(nombre);
            } else {
                espacios = espaciosDeportivosRepository.findAll();
            }
        }
        if (fecha != null && fecha.isEmpty()) {
            fecha = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        }
        if (fecha == null) {
            fecha = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        }
        String hoy = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        Map<Integer, Integer> primerasFotos = new HashMap<>();
        for (EspaciosDeportivos espacio : espacios) {
            ListaFotos lista = espacio.getListaFotos();
            if (lista != null && lista.getFotos() != null && !lista.getFotos().isEmpty()) {
                primerasFotos.put(espacio.getId(), lista.getFotos().get(0).getId());
            }
        }
        model.addAttribute("primerasFotos", primerasFotos);

        model.addAttribute("minDate", hoy);
        model.addAttribute("espacios", espacios);
        model.addAttribute("tipoSeleccionado", id);
        model.addAttribute("fechaSeleccionada", fecha);
        model.addAttribute("nombreSeleccionado", nombre);

        return "vecino/espacios";
    }

    @GetMapping("/detalles")
    public String espacioDetalles(Model model, @RequestParam(name = "idEspacio") int id, @RequestParam(name = "fecha") String fecha) {
        Optional<EspaciosDeportivos> optEspacio = espaciosDeportivosRepository.findById(id);
        Piscinas piscina;
        CanchasFutbol canchaFutbol;
        PistasAtletismo pista;
        Estadios estadio;

        if (optEspacio.isPresent()) {
            EspaciosDeportivos espacio = optEspacio.get();
            model.addAttribute("espacio", espacio);
            if (espacio.getTipoEspacio().getId() == 1) {
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
            List<Fotos> fotos = fotosRepository.findByListaFotosId(espacio.getListaFotos().getId());
            if (!fotos.isEmpty()) {
                Integer fotoId = fotos.get(0).getId();
                model.addAttribute("fotoId", fotoId);
            } else {
                model.addAttribute("fotoId", 0);
            }
        }
        return "vecino/detalles";
    }

    //*********************************************************************************************
    //
    //                                    Lista de Reservas
    //
    //*********************************************************************************************

    @GetMapping("/reservas")
    public String listarReservas(@RequestParam(value = "nombre", required = false) String nombre, Model model) {
        List<Reservas> reservas = (nombre == null || nombre.isEmpty()) ?
                reservasRepository.findAll() :
                reservasRepository.findByEspacioDeportivo_NombreContainingIgnoreCase(nombre);
        model.addAttribute("listaReservas", reservas);
        model.addAttribute("hoy", LocalDate.now());
        return "vecino/reservas";
    }

    @PostMapping("/cancelarreserva")
    public String cancelarReserva(@RequestParam Integer id, RedirectAttributes attr) {
        Optional<Reservas> optReserva = reservasRepository.findById(id);
        if (optReserva.isPresent()) {
            Reservas reserva = optReserva.get();
            LocalDate hoy = LocalDate.now();
            LocalDate fechaReserva = reserva.getFechaReserva();
            if (fechaReserva.isAfter(hoy)) {
                Optional<HorarioReservado> optHorarioReservado =
                        Optional.ofNullable(horarioReservadoRepository.findByHorario_IdAndFecha(
                                reserva.getHorario().getId(),
                                reserva.getFechaReserva()));
                if (optHorarioReservado.isPresent()) {
                    horarioReservadoRepository.delete(optHorarioReservado.get());
                    reservasRepository.delete(reserva);
                    attr.addFlashAttribute("msg", "Reserva cancelada correctamente. Su dinero será reembolsado en un plazo de dos semanas.");
                }
            } else {
                attr.addFlashAttribute("error", "No puede cancelar una reserva para hoy o en el pasado.");
            }
        } else {
            attr.addFlashAttribute("error", "No se encontró la reserva.");
        }
        return "redirect:/vecino/reservas";
    }

    //*********************************************************************************************
    //
    //                                    Reservar un Espacio
    //
    //*********************************************************************************************

    @GetMapping("/reservar")
    public String reservar(Model model, @ModelAttribute("reserva") Reservas reservas, HttpSession session, @RequestParam(name = "idEspacio") int idEspacio, @RequestParam(name = "fecha") String fecha) throws ParseException {
        LocalDate fechaconv = LocalDate.parse(fecha);
        if (fechaconv.isBefore(LocalDate.now())) {
            return "redirect:/vecino/";
        }
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Optional<EspaciosDeportivos> optespacio = espaciosDeportivosRepository.findById(idEspacio);

        if (optespacio.isPresent()) {
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        LocalDateTime fechaReserva = reserva.getFechaRegistro().toLocalDateTime();
        String fechaReservaString = fechaReserva.format(formatter);
        String asunto = "Confirmación de Reserva #" + reserva.getId();
        String cuerpo = "Id de Reserva #" + reserva.getId() + "\n" +
                fechaReservaString + "\n" +
                "Espacio: " + reserva.getEspacioDeportivo().getNombre() + "\n" +
                "Fecha de Reserva: " + reserva.getFechaReserva().toString() + "\n" +
                "Horario: " + reserva.getHorario().getHoraInicio() + "-" + reserva.getHorario().getHoraFin() + "\n" +
                "Medio de Pago: " + "Yape" + "\n" +
                "Total: S/." + reserva.getPago().getCantidad() + "25";


        emailService.enviarCorreo(reserva.getUsuario().getCorreo(), asunto, cuerpo);
        return "redirect:/vecino/espacios";
    }

    //*********************************************************************************************
    //
    //                                         Perfil
    //
    //*********************************************************************************************

    @GetMapping("/perfil")
    public String vecinoPerfil(@ModelAttribute("usuario") Usuario usuario, HttpSession session, Model model) {
        usuario = (Usuario) session.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        return "vecino/perfil";
    }

    @PostMapping("/guardarperfil")
    public String guardarPerfil(@ModelAttribute("usuario") @Valid Usuario usuario, BindingResult bindingResult, @RequestParam("archivo") MultipartFile file, Model model) {
        if (bindingResult.hasErrors()) {
            return "vecino/perfil";
        }

        if (file.isEmpty()) {
            return "vecino/perfil";
        }

        String fileName = file.getOriginalFilename();

        if (fileName.contains("..")) {
            model.addAttribute("msg", "Debe ingresar un archivo válido");
            return "vecino/perfil";
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.matches("image/(jpeg|png|jpg|gif|bmp|webp)")) {
            model.addAttribute("msg", "El archivo debe ser una imagen (jpg, png, gif, etc)");
            return "vecino/perfil";
        }

        try {
            usuario.setFoto(file.getBytes());
            usuario.setFotoNombre(fileName);
            usuario.setFotoTipoArchivo(contentType);
            usuarioRepository.save(usuario);
            return "redirect:/vecino/perfil";
        } catch (Exception e) {
            e.printStackTrace();
            return "vecino/perfil";
        }
    }

    //*********************************************************************************************
    //
    //                                    Gestión de Imágenes
    //
    //*********************************************************************************************

    @GetMapping("/profileimage/{id}")
    public ResponseEntity<byte[]> mostrarImagenPefil(@PathVariable("id") Integer id) {
        Optional<Usuario> optusuario = usuarioRepository.findById(id);
        if (optusuario.isPresent()) {
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

    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> mostrarImagen(@PathVariable("id") Integer id) {
        Optional<Fotos> optfotos = fotosRepository.findById(id);
        if (optfotos.isPresent()) {
            Fotos fotos = optfotos.get();

            byte[] image = fotos.getFoto();
            if (image == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            String tipoArchivo = fotos.getFotoTipoArchivo();
            if (tipoArchivo == null || tipoArchivo.isBlank()) {
                tipoArchivo = "application/octet-stream";
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


    @PostMapping("/api/chatbot")
    @ResponseBody
    public ResponseEntity<Map<String, String>> preguntar(@RequestBody Map<String, String> body) {
        String pregunta = body.get("pregunta");
        Map<String, String> json = new HashMap<>();

        if (pregunta == null || pregunta.trim().isEmpty()) {
            json.put("respuestaBot", "La pregunta está vacía.");
            return ResponseEntity.badRequest().body(json);
        }

        try {
            ChatMessageDTO dto = new ChatMessageDTO();
            dto.setMensajeUsuario(pregunta);
            String respuesta = openAiService.generarRespuesta(dto);
            json.put("respuestaBot", respuesta);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            e.printStackTrace();
            json.put("respuestaBot", "Error al procesar la pregunta.");
            return ResponseEntity.status(500).body(json);
        }
    }



}
