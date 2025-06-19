package com.example.gtics_ta.Controllers;

import com.example.gtics_ta.DTO.HorariosConsultaDTO;
import com.example.gtics_ta.Entity.*;
import com.example.gtics_ta.Repository.*;
import com.example.gtics_ta.Services.MailService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
    @Autowired
    private SuscripcionesRepository suscripcionesRepository;
    @Autowired
    private GimnasiosRepository gimnasiosRepository;
    @Autowired
    private MediosPagoRepository mediosPagoRepository;
    @Autowired
    private PagosRepository pagosRepository;

    @GetMapping(value = {"","/"})
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
    public String espacioDetalles(Model model, @RequestParam(name = "idEspacio") int id, @RequestParam(name = "fecha") String fecha){
        Optional<EspaciosDeportivos> optEspacio = espaciosDeportivosRepository.findById(id);
        Piscinas piscina;
        CanchasFutbol canchaFutbol;
        PistasAtletismo pista;
        Estadios estadio;
        Gimnasios gimnasio;

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
            } else if (espacio.getTipoEspacio().getId() == 5) {
                gimnasio = gimnasiosRepository.findByIdEspacios(espacio.getId());
                model.addAttribute("gimnasio", gimnasio);
            }
            model.addAttribute("fecha", fecha);
            List<Fotos> fotos = fotosRepository.findByListaFotosId(espacio.getListaFotos().getId());
            if(!fotos.isEmpty()) {
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
    public String listarReservas(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        List<Reservas> reservas = reservasRepository.findByUsuarioId(usuario.getId());
        List<Suscripciones> suscripciones = suscripcionesRepository.findByUsuarioId(usuario.getId());
        model.addAttribute("listaReservas", reservas);
        model.addAttribute("listaSuscripciones", suscripciones);
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
                attr.addFlashAttribute("error", "Solo puede cancelar una reserva con un plazo de antelación de un día.");
            }
        } else {
            attr.addFlashAttribute("error", "No se encontró la reserva.");
        }
        return "redirect:/vecino/reservas";
    }

    @PostMapping("/cancelarsuscripcion")
    public String cancelarSuscripcion(@RequestParam Integer id, RedirectAttributes attr) {
        Optional<Suscripciones> optSub = suscripcionesRepository.findById(id);
        if (optSub.isPresent()){
            Suscripciones suscripciones = optSub.get();
            LocalDate hoy = LocalDate.now();
            LocalDate fechaFin = suscripciones.getFechaFin();
            if(fechaFin.isAfter(hoy)){
                suscripciones.setEstado(false);
                attr.addFlashAttribute("msg", "Suscripción cancelada correctamente.");
            } else {
                attr.addFlashAttribute("error", "No deberías poder ver esto.");
            }
        } else {
            attr.addFlashAttribute("error", "No se encontró la suscripción.");
        }
        return "redirect:/vecino/reservas";
    }

    //*********************************************************************************************
    //
    //                                    Reservar un Espacio
    //
    //*********************************************************************************************

    @GetMapping("/reservar")
    public String reservar(Model model, @ModelAttribute("reserva") Reservas reserva, HttpSession session, @RequestParam(name = "idEspacio") int idEspacio, @RequestParam(name = "fecha") String fecha) throws ParseException {
        LocalDate fechaconv = LocalDate.parse(fecha);
        if(fechaconv.isBefore(LocalDate.now())) {
            return "redirect:/vecino/";
        }
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Optional<EspaciosDeportivos> optespacio = espaciosDeportivosRepository.findById(idEspacio);

        if( optespacio.isPresent()) {
            EspaciosDeportivos espacio = optespacio.get();
            List<HorariosConsultaDTO> listaHorarios = horariosRepository.obtenerHorariosConsulta(fechaconv, espacio.getId());
            reserva.setUsuario(usuario);
            reserva.setEspacioDeportivo(espacio);
            reserva.setFechaReserva(fechaconv);
            model.addAttribute("reserva", reserva);
            model.addAttribute("listaHorarios", listaHorarios);
            String hoy = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            model.addAttribute("minDate", hoy);
        } else {
            return "redirect:/vecino/";
        }
        return "vecino/reservar";
    }

    @GetMapping("/horarios-disponibles")
    @ResponseBody
    public List<HorariosConsultaDTO> obtenerHorariosPorFecha(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("idEspacio") Integer idEspacio) {

        return horariosRepository.obtenerHorariosConsulta(fecha, idEspacio);
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
        Optional<MediosPago> optMedioPago = mediosPagoRepository.findById(1);
        MediosPago mediosPago = optMedioPago.get();
        pago.setCantidad(reserva.getEspacioDeportivo().getCostoHorario());
        pago.setMedioPago(mediosPago);
        pagosRepository.save(pago);
        reserva.setPago(pago);

        horarioReservadoRepository.save(horarioReservado);
        reservasRepository.save(reserva);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        LocalDateTime fechaReserva = reserva.getFechaRegistro().toLocalDateTime();
        String fechaReservaString = fechaReserva.format(formatter);
        String asunto = "Confirmación de Reserva #" + reserva.getId();
        Map<String, Object> datos = new HashMap<>();
        datos.put("idReserva", reserva.getId());
        datos.put("nombreEspacio", reserva.getEspacioDeportivo().getNombre());
        datos.put("fechaReserva", fechaReservaString);
        datos.put("horario", reserva.getHorario().getHoraInicio() + "-" + reserva.getHorario().getHoraFin());
        datos.put("medioPago", pago.getMedioPago().getNombre());
        datos.put("total", reserva.getEspacioDeportivo().getCostoHorario());
        emailService.enviarCorreoConPlantilla(reserva.getUsuario().getCorreo(), asunto, "email/reserva", datos);

        return "redirect:/vecino/espacios";
    }

    //*********************************************************************************************
    //
    //                                    Suscribirse
    //
    //*********************************************************************************************

    @GetMapping("/suscribirse")
    public String suscribirse(Model model, @ModelAttribute("suscripcion") Suscripciones suscripcion, HttpSession session, @RequestParam("idEspacio") Integer idEspacio) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        Optional<EspaciosDeportivos> optEspacio = espaciosDeportivosRepository.findById(idEspacio);
        if(optEspacio.isPresent()) {
            EspaciosDeportivos espacio = optEspacio.get();
            Gimnasios gimnasio = gimnasiosRepository.findByIdEspacios(espacio.getId());
            suscripcion.setUsuario(usuario);
            suscripcion.setEspacio(espacio);
            model.addAttribute("gimnasio", gimnasio);
            model.addAttribute("suscripcion", suscripcion);
            model.addAttribute("minDate", LocalDate.now());
            return "vecino/suscribirse";
        } else {
            return "redirect:/vecino/";
        }
    }

    @PostMapping("/guardarsuscripcion")
    public String guardarSuscripcion(@ModelAttribute("suscripcion") Suscripciones suscripcion){
        suscripcion.setFechaRegistro(LocalDateTime.now());
        LocalDate fechaFin = switch (suscripcion.getTipoSuscripcion().toUpperCase()) {
            case "SEMANAL" -> suscripcion.getFechaInicio().plusDays(7);
            case "MENSUAL" -> suscripcion.getFechaInicio().plusMonths(1);
            case "ANUAL" -> suscripcion.getFechaInicio().plusYears(1);
            default -> suscripcion.getFechaInicio();
        };
        suscripcion.setFechaFin(fechaFin);
        suscripcion.setEstado(true);

        Gimnasios gimnasio = gimnasiosRepository.findByIdEspacios(suscripcion.getEspacio().getId());

        Pagos pago = new Pagos();
        float costo = switch (suscripcion.getTipoSuscripcion()) {
            case "SEMANAL" -> gimnasio.getCostoSemanal();
            case "MENSUAL" -> gimnasio.getCostoMensual();
            case "ANUAL" -> gimnasio.getCostoAnual();
            default -> 0;
        };
        pago.setCantidad(costo);
        Optional<MediosPago> optMedioPago = mediosPagoRepository.findById(1);
        MediosPago mediosPago = optMedioPago.get();
        pago.setMedioPago(mediosPago);
        pagosRepository.save(pago);
        suscripcion.setPagos(pago);

        suscripcionesRepository.save(suscripcion);

        String asunto = "Confirmación de Suscripción #" + suscripcion.getId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        LocalDateTime fechaRegistro = suscripcion.getFechaRegistro();
        String fechaRegistroStr = fechaRegistro.format(formatter);
        Map<String, Object> datos = new HashMap<>();
        datos.put("idSuscripcion", suscripcion.getId());
        datos.put("nombreGimnasio", suscripcion.getEspacio().getNombre());
        datos.put("fechaInicio", suscripcion.getFechaInicio().format(formatter));
        datos.put("fechaFin", suscripcion.getFechaFin().format(formatter));
        datos.put("tipoSuscripcion", suscripcion.getTipoSuscripcion());
        datos.put("fechaPago", fechaRegistroStr);
        datos.put("medioPago", pago.getMedioPago().getNombre());
        datos.put("costoTotal", pago.getCantidad());
        emailService.enviarCorreoConPlantilla(suscripcion.getUsuario().getCorreo(), asunto, "email/suscripcion", datos);
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
    public String guardarPerfil(@ModelAttribute("usuario") @Valid Usuario usuario, BindingResult bindingResult, @RequestParam("archivo") MultipartFile file , Model model) {
        if(bindingResult.hasErrors()) {
            return "vecino/perfil";
        }

        if(file.isEmpty()) {
            return "vecino/perfil";
        }

        String fileName = file.getOriginalFilename();

        assert fileName != null;
        if (fileName.contains("..")){
            model.addAttribute("msg","Debe ingresar un archivo válido");
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

    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> mostrarImagen(@PathVariable("id") Integer id) {
        Optional<Fotos> optfotos = fotosRepository.findById(id);
        if(optfotos.isPresent()) {
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

        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
