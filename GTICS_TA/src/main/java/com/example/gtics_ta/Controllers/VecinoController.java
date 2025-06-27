package com.example.gtics_ta.Controllers;

import com.example.gtics_ta.DTO.ChatMessageDTO;
import com.example.gtics_ta.DTO.HorariosConsultaDTO;
import com.example.gtics_ta.Entity.*;
import com.example.gtics_ta.Repository.*;
import com.example.gtics_ta.Services.OpenAiService;
import com.example.gtics_ta.Services.MailService;
import com.example.gtics_ta.Services.ImageService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

import java.math.BigDecimal;
import java.net.URI;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import jakarta.servlet.http.HttpServletRequest;

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
    @Autowired
    private SuscripcionesRepository suscripcionesRepository;
    @Autowired
    private GimnasiosRepository gimnasiosRepository;
    @Autowired
    private ImageService imageService;
    @Autowired
    private MediosPagoRepository mediosPagoRepository;
    @Autowired
    private PagosRepository pagosRepository;
    @Autowired
    private NotificacionesRepository notificacionesRepository;

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
    //                                Lista de Reservas/Sucripciones
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

        // 👇 Cargar notificaciones aquí
        cargarNotificaciones(model, session);
        return "vecino/reservas";
    }

    @GetMapping("/reservas/detalles/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerDetallesReserva(@PathVariable Integer id) {
        try {
            Optional<Reservas> optReserva = reservasRepository.findById(id);
            if (optReserva.isPresent()) {
                Reservas reserva = optReserva.get();
                Map<String, Object> detalles = new HashMap<>();
                // Información de la reserva
                detalles.put("id", reserva.getId());
                detalles.put("espacio", reserva.getEspacioDeportivo().getNombre());
                detalles.put("fechaReserva", reserva.getFechaReserva());
                detalles.put("horario", reserva.getHorario().getHoraInicio() + " - " + reserva.getHorario().getHoraFin());
                detalles.put("fechaRegistro", reserva.getFechaRegistro());

                // Información del pago
                if (reserva.getPago() != null) {
                    Pagos pago = reserva.getPago();
                    detalles.put("idPago", pago.getId());
                    detalles.put("montoPagado", pago.getCantidad());
                    detalles.put("estadoPago", pago.getEstadoPago().name());
                    detalles.put("fechaPago", pago.getFechaPago());
                    detalles.put("medioPago", pago.getMedioPago().getNombre());
                    detalles.put("numeroTransaccion", pago.getNumeroTransaccion());

                    // Si hay fotos de comprobantes, agregar información
                    if (pago.getListaFotosComprobantes() != null &&
                            pago.getListaFotosComprobantes().getFotos() != null &&
                            !pago.getListaFotosComprobantes().getFotos().isEmpty()) {
                        detalles.put("tieneComprobantes", true);

                        // Agregar URLs de las fotos
                        List<Map<String, String>> comprobantes = new ArrayList<>();
                        for (Fotos foto : pago.getListaFotosComprobantes().getFotos()) {
                            Map<String, String> comprobante = new HashMap<>();
                            comprobante.put("url", foto.getFotoUrl());
                            comprobante.put("nombre", foto.getFotoNombre());
                            comprobantes.add(comprobante);
                        }
                        detalles.put("comprobantes", comprobantes);
                    } else {
                        detalles.put("tieneComprobantes", false);
                    }
                }

                return ResponseEntity.ok(detalles);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Error al obtener detalles de la reserva: " + e.getMessage()));
        }
    }

    @GetMapping("/reservas/detallesubs/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerDetallesSuscripciones(@PathVariable Integer id) {
        try {
            Optional<Suscripciones> optSub = suscripcionesRepository.findById(id);
            if (optSub.isPresent()) {
                Suscripciones suscripcion = optSub.get();
                Map<String, Object> detalles = new HashMap<>();

                // Información de la reserva
                detalles.put("id", suscripcion.getId());
                detalles.put("espacio", suscripcion.getEspacio().getNombre());
                detalles.put("tipoSuscripcion", suscripcion.getTipoSuscripcion());
                detalles.put("fechaInicio", suscripcion.getFechaInicio());
                detalles.put("fechaFin", suscripcion.getFechaFin());
                detalles.put("fechaRegistro", suscripcion.getFechaRegistro());

                // Información del pago
                if (suscripcion.getPagos() != null) {
                    Pagos pago = suscripcion.getPagos();
                    detalles.put("idPago", pago.getId());
                    detalles.put("montoPagado", pago.getCantidad());
                    detalles.put("estadoPago", pago.getEstadoPago().name());
                    detalles.put("fechaPago", pago.getFechaPago());
                    detalles.put("medioPago", pago.getMedioPago().getNombre());
                    detalles.put("numeroTransaccion", pago.getNumeroTransaccion());

                    // Si hay fotos de comprobantes, agregar información
                    if (pago.getListaFotosComprobantes() != null &&
                            pago.getListaFotosComprobantes().getFotos() != null &&
                            !pago.getListaFotosComprobantes().getFotos().isEmpty()) {
                        detalles.put("tieneComprobantes", true);

                        // Agregar URLs de las fotos
                        List<Map<String, String>> comprobantes = new ArrayList<>();
                        for (Fotos foto : pago.getListaFotosComprobantes().getFotos()) {
                            Map<String, String> comprobante = new HashMap<>();
                            comprobante.put("url", foto.getFotoUrl());
                            comprobante.put("nombre", foto.getFotoNombre());
                            comprobantes.add(comprobante);
                        }
                        detalles.put("comprobantes", comprobantes);
                    } else {
                        detalles.put("tieneComprobantes", false);
                    }
                }

                return ResponseEntity.ok(detalles);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Error al obtener detalles de la suscripcion: " + e.getMessage()));
        }
    }

    private void cargarNotificaciones(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            List<Notificaciones> notificaciones = notificacionesRepository.findByUsuarioOrderByFechaCreacionDesc(usuario);
            model.addAttribute("notificaciones", notificaciones);
        }
    }

    @PostMapping("/cancelarreserva")
    public String cancelarReserva(@RequestParam("id") Integer id, RedirectAttributes attr) {
        Optional<Reservas> optReserva = reservasRepository.findById(id);
        LocalDate hoy = LocalDate.now();
        if (optReserva.isPresent()) {
            Reservas reserva = optReserva.get();
            LocalDate fechaReserva = reserva.getFechaReserva();
            if (fechaReserva.isAfter(hoy)) {
                Optional<HorarioReservado> optHorarioReservado =
                        Optional.ofNullable(horarioReservadoRepository.findByHorario_IdAndFecha(
                                reserva.getHorario().getId(),
                                reserva.getFechaReserva()));
                if (optHorarioReservado.isPresent()) {
                    horarioReservadoRepository.delete(optHorarioReservado.get());
                    reserva.setEstadoReserva(Reservas.EstadoReserva.CANCELADA_USUARIO);
                    reservasRepository.save(reserva);
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
            suscripciones.setEstado(false);
            suscripcionesRepository.save(suscripciones);
            attr.addFlashAttribute("msg", "Suscripción cancelada correctamente.");

        } else {
            attr.addFlashAttribute("error", "No se encontró la suscripción.");
            System.out.println("No se encontró la suscripcion");
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

            // Cargar medios de pago activos
            List<MediosPago> mediosPago = mediosPagoRepository.findAll().stream()
                .filter(MediosPago::getActivo)
                .toList();
            model.addAttribute("mediosPago", mediosPago);
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
    public String guardarreserva(@ModelAttribute("reserva") Reservas reserva,
                                @RequestParam("medioPagoId") int medioPagoId,
                                @RequestParam(value = "numeroTarjeta", required = false) String numeroTarjeta,
                                @RequestParam(value = "nombreTarjeta", required = false) String nombreTarjeta,
                                @RequestParam(value = "fechaExpiracion", required = false) String fechaExpiracion,
                                @RequestParam(value = "cvv", required = false) String cvv,
                                @RequestParam(value = "numeroTransaccion", required = false) String numeroTransaccion,
                                @RequestParam(value = "comprobantes", required = false) MultipartFile[] comprobantes,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            reserva.setFechaRegistro(timestamp);

            // Obtener medio de pago seleccionado
            Optional<MediosPago> optMedioPago = mediosPagoRepository.findById(medioPagoId);
            if (!optMedioPago.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Medio de pago no válido");
                return "redirect:/vecino/espacios";
            }

            MediosPago medioPago = optMedioPago.get();

            // Crear pago
            Pagos pago = new Pagos();
            pago.setMedioPago(medioPago);
            pago.setCantidad(BigDecimal.valueOf(reserva.getEspacioDeportivo().getCostoHorario()));
            pago.setIpUsuario(request.getRemoteAddr());
            pago.setUserAgent(request.getHeader("User-Agent"));

            // Procesar según tipo de pago
            if (medioPago.getTipoPago() == MediosPago.TipoPago.AUTOMATICO) {
                // Pago con tarjeta - simular pasarela
                pago.setEstadoPago(Pagos.EstadoPago.APROBADO);
                pago.setCodigoAutorizacion("AUTH" + System.currentTimeMillis());
                pago.setDatosPasarela("{\"numeroTarjeta\":\"****" + numeroTarjeta.substring(numeroTarjeta.length() - 4) + "\",\"nombreTarjeta\":\"" + nombreTarjeta + "\"}");
                pago.setFechaVerificacion(timestamp);
            } else {
                // Pago manual - requiere verificación
                pago.setEstadoPago(Pagos.EstadoPago.PENDIENTE);
                pago.setNumeroTransaccion(numeroTransaccion);

                // Subir comprobantes si existen
                if (comprobantes != null && comprobantes.length > 0 && !comprobantes[0].isEmpty()) {
                    try {
                        ListaFotos listaFotosComprobantes = imageService.uploadPaymentReceipts(comprobantes);
                        pago.setListaFotosComprobantes(listaFotosComprobantes);
                    } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("error", "Error al subir comprobantes: " + e.getMessage());
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                        return "redirect:/vecino/espacios";
                    }
                }
            }

            // Guardar pago
            Pagos pagoGuardado = pagosRepository.save(pago);
            reserva.setPago(pagoGuardado);

            // Crear horario reservado
            HorarioReservado horarioReservado = new HorarioReservado();
            horarioReservado.setFecha(reserva.getFechaReserva());
            horarioReservado.setHorario(reserva.getHorario());

            horarioReservadoRepository.save(horarioReservado);
            reservasRepository.save(reserva);
            String titulo = "Reserva Exitosa";
            String mensaje = "Reserva del espacio deportivo \"" + reserva.getEspacioDeportivo().getNombre() + "\" para el día " + reserva.getFechaReserva().toString();

            Notificaciones notificacion = new Notificaciones(
                    reserva.getUsuario(),
                    Notificaciones.TipoNotificacion.RECORDATORIO_RESERVA,
                    titulo,
                    mensaje,
                    reserva
            );
            notificacionesRepository.save(notificacion);

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

            if (medioPago.getTipoPago() == MediosPago.TipoPago.AUTOMATICO) {
                redirectAttributes.addFlashAttribute("success", "Reserva confirmada. Pago procesado exitosamente.");
            } else {
                redirectAttributes.addFlashAttribute("success", "Reserva creada. Su pago está pendiente de verificación por el administrador.");
            }

            return "redirect:/vecino/reservas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la reserva: " + e.getMessage());
            e.printStackTrace();

            System.out.println(e.getMessage());
            return "redirect:/vecino/espacios";
        }
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
            List<MediosPago> mediosPago = mediosPagoRepository.findAll().stream()
                    .filter(MediosPago::getActivo)
                    .toList();
            model.addAttribute("mediosPago", mediosPago);
            return "vecino/suscribirse";
        } else {
            return "redirect:/vecino/";
        }
    }

    @PostMapping("/guardarsuscripcion")
    public String guardarSuscripcion(@ModelAttribute("suscripcion") Suscripciones suscripcion,
                                     @RequestParam("medioPagoId") int medioPagoId,
                                     @RequestParam(value = "numeroTarjeta", required = false) String numeroTarjeta,
                                     @RequestParam(value = "nombreTarjeta", required = false) String nombreTarjeta,
                                     @RequestParam(value = "fechaExpiracion", required = false) String fechaExpiracion,
                                     @RequestParam(value = "cvv", required = false) String cvv,
                                     @RequestParam(value = "numeroTransaccion", required = false) String numeroTransaccion,
                                     @RequestParam(value = "comprobantes", required = false) MultipartFile[] comprobantes,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes) {
        Gimnasios gimnasio = gimnasiosRepository.findByIdEspacios(suscripcion.getEspacio().getId());
        float costo = switch (suscripcion.getTipoSuscripcion()) {
            case "SEMANAL" -> gimnasio.getCostoSemanal();
            case "MENSUAL" -> gimnasio.getCostoMensual();
            case "ANUAL" -> gimnasio.getCostoAnual();
            default -> 0;
        };

        // Obtener medio de pago seleccionado
        Optional<MediosPago> optMedioPago = mediosPagoRepository.findById(medioPagoId);
        if (!optMedioPago.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Medio de pago no válido");
            return "redirect:/vecino/espacios";
        }

        MediosPago medioPago = optMedioPago.get();

        // Crear pago
        Pagos pago = new Pagos();
        pago.setMedioPago(medioPago);
        pago.setCantidad(BigDecimal.valueOf(costo));
        pago.setIpUsuario(request.getRemoteAddr());
        pago.setUserAgent(request.getHeader("User-Agent"));

        // Procesar según tipo de pago
        if (medioPago.getTipoPago() == MediosPago.TipoPago.AUTOMATICO) {
            // Pago con tarjeta - simular pasarela
            pago.setEstadoPago(Pagos.EstadoPago.APROBADO);
            pago.setCodigoAutorizacion("AUTH" + System.currentTimeMillis());
            pago.setDatosPasarela("{\"numeroTarjeta\":\"****" + numeroTarjeta.substring(numeroTarjeta.length() - 4) + "\",\"nombreTarjeta\":\"" + nombreTarjeta + "\"}");
            pago.setFechaVerificacion(Timestamp.valueOf(LocalDateTime.now()));
        } else {
            // Pago manual - requiere verificación
            pago.setEstadoPago(Pagos.EstadoPago.PENDIENTE);
            pago.setNumeroTransaccion(numeroTransaccion);

            // Subir comprobantes si existen
            if (comprobantes != null && comprobantes.length > 0 && !comprobantes[0].isEmpty()) {
                try {
                    ListaFotos listaFotosComprobantes = imageService.uploadPaymentReceipts(comprobantes);
                    pago.setListaFotosComprobantes(listaFotosComprobantes);
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("error", "Error al subir comprobantes: " + e.getMessage());
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    return "redirect:/vecino/espacios";
                }
            }
        }

        // Guardar pago
        Pagos pagoGuardado = pagosRepository.save(pago);
        suscripcion.setPagos(pagoGuardado);

        suscripcion.setFechaRegistro(LocalDateTime.now());
        LocalDate fechaFin = switch (suscripcion.getTipoSuscripcion().toUpperCase()) {
            case "SEMANAL" -> suscripcion.getFechaInicio().plusDays(7);
            case "MENSUAL" -> suscripcion.getFechaInicio().plusMonths(1);
            case "ANUAL" -> suscripcion.getFechaInicio().plusYears(1);
            default -> suscripcion.getFechaInicio();
        };
        suscripcion.setFechaFin(fechaFin);
        suscripcion.setEstado(true);


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
    public String guardarPerfil(@ModelAttribute("usuario") @Valid Usuario usuario, BindingResult bindingResult, @RequestParam("archivo") MultipartFile file, HttpSession session, Model model) {
        if(bindingResult.hasErrors()) {
            return "vecino/perfil";
        }
        usuarioRepository.save(usuario);
        if(!file.isEmpty()){
            try {
                // Obtener usuario real de la sesión
                Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
                if (usuarioSesion == null) {
                    model.addAttribute("msg", "Sesión expirada");
                    return "vecino/perfil";
                }

                // Usar el nuevo servicio de imágenes con S3
                imageService.uploadUserProfileImage(usuarioSesion, file);

                // Actualizar usuario en sesión
                session.setAttribute("usuario", usuarioSesion);

                model.addAttribute("msg", "Imagen de perfil actualizada exitosamente");
                return "redirect:/vecino/perfil";
            } catch (Exception e) {
                model.addAttribute("msg", "Error al subir la imagen: " + e.getMessage());
                e.printStackTrace(); // Para ver el error en consola
                return "vecino/perfil";
            }
        }
        return "vecino/perfil";
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

            // Si tiene URL de S3, redirigir
            if (usuario.getFotoUrl() != null && !usuario.getFotoUrl().isEmpty()) {
                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(usuario.getFotoUrl()))
                        .build();
            }

            // Fallback para imágenes BLOB (migración)
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


    //*********************************************************************************************
    //
    //                                       ChatBot
    //
    //*********************************************************************************************



    @PostMapping("/api/chatbot")
    @ResponseBody
    public ResponseEntity<Map<String, String>> preguntar(@RequestBody Map<String, String> body,
                                                         @CookieValue(value = "espacioDetectado", required = false) String espacioCookie,
                                                         HttpServletResponse response) {
        String pregunta = body.get("pregunta");
        Map<String, String> json = new HashMap<>();

        if (pregunta == null || pregunta.trim().isEmpty()) {
            json.put("respuestaBot", "La pregunta está vacía.");
            return ResponseEntity.badRequest().body(json);
        }

        try {
            ChatMessageDTO dto = new ChatMessageDTO();
            dto.setMensajeUsuario(pregunta);

            // Si el espacio no está en la pregunta, intenta usar el de la cookie
            String espacioDetectado = openAiService.detectarEspacio(pregunta);
            if (espacioDetectado == null && espacioCookie != null) {
                dto.setEspacioDetectado(espacioCookie);
            } else {
                dto.setEspacioDetectado(espacioDetectado);
            }

            // Generar respuesta
            String respuesta = openAiService.generarRespuesta(dto);
            json.put("respuestaBot", respuesta);

            // Si se detectó un nuevo espacio, guardarlo como cookie por 30 min
            if (dto.getEspacioDetectado() != null) {
                Cookie cookie = new Cookie("espacioDetectado", dto.getEspacioDetectado());
                cookie.setMaxAge(30 * 60); // 30 minutos
                cookie.setPath("/"); // visible para todo el sitio
                response.addCookie(cookie);
            }
            return ResponseEntity.ok(json);

        } catch (Exception e) {
            e.printStackTrace();
            json.put("respuestaBot", "Error al procesar la pregunta.");
            return ResponseEntity.status(500).body(json);
        }
    }




}
