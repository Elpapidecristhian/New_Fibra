package com.example.gtics_ta.Controllers;

import com.example.gtics_ta.DTO.AdminDTO;
import com.example.gtics_ta.DTO.ServicioDTO;
import com.example.gtics_ta.Entity.*;
import com.example.gtics_ta.Repository.*;
import com.example.gtics_ta.Services.ImageService;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.properties.*;
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

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EspaciosDeportivosRepository espaciosRepository;

    @Autowired
    private TipoEspacioRepository tipoEspacioRepository;

    @Autowired
    private PiscinasRepository piscinaRepository;

    @Autowired
    private CanchasFutbolRepository canchasFutbolRepository;

    @Autowired
    private ListaFotosRepository listaFotosRepository;

    @Autowired
    private ReservasRepository reservaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private HorariosRepository horariosRepository;
    @Autowired
    private EspaciosDeportivosRepository espaciosDeportivosRepository;
    @Autowired
    private PistasAtletismoRepository pistasAtletismoRepository;
    @Autowired
    private EstadiosRepository estadiosRepository;
    @Autowired
    private FotosRepository fotosRepository;
    @Autowired
    private HorarioReservadoRepository horarioReservadoRepository;
    @Autowired
    private PagosRepository pagosRepository;

    @Autowired
    private ImageService imageService;






    // DASHBOARD PRINCIPAL
    @GetMapping(value = {"","/"})
    public String dashboard(Model model) {
        // Crear objeto AdminDTO con datos básicos
        AdminDTO dashboard = new AdminDTO();

        // Datos básicos
        List<EspaciosDeportivos> espacios = espaciosRepository.findAll();
        List<Reservas> reservas = reservaRepository.findAll();

        dashboard.setTotalUsuarios(0); // TODO: implementar conteo de usuarios
        dashboard.setTotalUsuariosBaneados(0); // TODO: implementar conteo de usuarios baneados
        dashboard.setCantidadTotalReservas(reservas.size());
        dashboard.setEspaciosDisponibles(espacios.size());

        // Datos para gráficos (valores por defecto)
        dashboard.setNombresServiciosTop(List.of("Piscina", "Cancha", "Pista"));
        dashboard.setCantidadReservasTop(List.of(10L, 8L, 5L));
        dashboard.setNombresServiciosPorcentaje(List.of("Piscina", "Cancha", "Pista"));
        dashboard.setCantidadServiciosPorcentaje(List.of(10L, 8L, 5L));
        dashboard.setHorasReservas(List.of("08:00", "09:00", "10:00", "11:00"));
        dashboard.setCantidadReservasPorHora(List.of(2L, 5L, 8L, 3L));
        dashboard.setNombresUsuariosTop(List.of("Usuario1", "Usuario2", "Usuario3"));
        dashboard.setCantidadReservasUsuariosTop(List.of(5L, 3L, 2L));

        model.addAttribute("dashboard", dashboard);
        model.addAttribute("totalEspacios", espacios.size());
        model.addAttribute("totalReservas", reservas.size());
        model.addAttribute("listaEspacios", espacios);
        model.addAttribute("listaReservas", reservas);

        return "admin/dashboard";
    }

    // DASHBOARD ALTERNATIVO
    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        return dashboard(model);
    }

    // PERFIL DE USUARIO ADMIN
    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);
        return "admin/perfil";
    }

    @PostMapping("/guardarperfil")
    public String guardarPerfil(@ModelAttribute("usuario") @Valid Usuario usuario, BindingResult bindingResult,
                               @RequestParam("archivo") MultipartFile file, HttpSession session, Model model) {
        if(bindingResult.hasErrors()) {
            return "admin/perfil";
        }

        if(file.isEmpty()) {
            model.addAttribute("msg", "Debe seleccionar una imagen");
            return "admin/perfil";
        }

        try {
            // Obtener usuario real de la sesión
            Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
            if (usuarioSesion == null) {
                model.addAttribute("msg", "Sesión expirada");
                return "admin/perfil";
            }

            // Usar el nuevo servicio de imágenes con S3
            imageService.uploadUserProfileImage(usuarioSesion, file);

            // Actualizar usuario en sesión
            session.setAttribute("usuario", usuarioSesion);

            model.addAttribute("msg", "Imagen de perfil actualizada exitosamente");
            return "redirect:/admin/perfil";
        } catch (Exception e) {
            model.addAttribute("msg", "Error al subir la imagen: " + e.getMessage());
            e.printStackTrace(); // Para ver el error en consola
            return "admin/perfil";
        }
    }

    @GetMapping("/profileimage/{id}")
    public ResponseEntity<byte[]> mostrarImagenPerfil(@PathVariable("id") Integer id) {
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

            return new ResponseEntity<>(image, httpHeaders, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // LISTAR SERVICIOS
    @GetMapping("/servicios")
    public String listarServicios(Model model) {
        List<EspaciosDeportivos> espacios = espaciosRepository.findAll();
        model.addAttribute("listaEspacios", espacios);
        return "admin/servicios";
    }

    // LISTAR RESERVAS
    @GetMapping("/reservas")
    public String listarReservas(@RequestParam(value = "nombre", required = false) String nombre,
                                @RequestParam(value = "tipoEspacio", required = false) Integer tipoEspacio,
                                Model model) {
        try {
            // Actualizar reservas completadas antes de mostrar la lista
            actualizarReservasCompletadas();

            List<Reservas> reservas;

            // Filtrar por tipo de espacio y nombre si se proporcionan
            if (tipoEspacio != null && (nombre != null && !nombre.isEmpty())) {
                reservas = reservaRepository.findByEspacioDeportivo_TipoEspacio_IdAndEspacioDeportivo_NombreContainingIgnoreCase(tipoEspacio, nombre);
            } else if (tipoEspacio != null) {
                reservas = reservaRepository.findByEspacioDeportivo_TipoEspacio_Id(tipoEspacio);
            } else if (nombre != null && !nombre.isEmpty()) {
                reservas = reservaRepository.findByEspacioDeportivo_NombreContainingIgnoreCase(nombre);
            } else {
                reservas = reservaRepository.findAll();
            }

            model.addAttribute("listaReservas", reservas);

            // Cargar tipos de espacios para los botones de filtro
            List<TipoEspacio> tiposEspacio = tipoEspacioRepository.findAllByOrderByNombreAsc();
            model.addAttribute("tiposEspacio", tiposEspacio);

            System.out.println("Número de reservas encontradas: " + reservas.size());
        } catch (Exception e) {
            System.err.println("Error al cargar reservas: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("listaReservas", Collections.emptyList());
            model.addAttribute("tiposEspacio", Collections.emptyList());
        }
        return "admin/reservas";
    }

    // APROBAR PAGO
    @PostMapping("/reservas/aprobar-pago/{id}")
    @ResponseBody
    public ResponseEntity<String> aprobarPago(@PathVariable Integer id, HttpSession session) {
        try {
            Optional<Pagos> optPago = pagosRepository.findById(id);
            if (optPago.isPresent()) {
                Pagos pago = optPago.get();
                Usuario admin = (Usuario) session.getAttribute("usuario");

                // Actualizar estado del pago
                pago.setEstadoPago(Pagos.EstadoPago.APROBADO);
                pago.setFechaVerificacion(new Timestamp(System.currentTimeMillis()));
                pago.setVerificadoPor(admin);
                pago.setObservacionesAdmin(null); // Limpiar observaciones

                pagosRepository.save(pago);

                // Buscar y actualizar la reserva asociada
                List<Reservas> reservasConPago = reservaRepository.findByPago_Id(id);

                for (Reservas reserva : reservasConPago) {
                    System.out.println("Procesando reserva ID: " + reserva.getId() + " con estado: " + reserva.getEstadoReserva());

                    // Si la reserva estaba cancelada por admin, reactivarla
                    if (reserva.getEstadoReserva() != null &&
                        reserva.getEstadoReserva().equals(Reservas.EstadoReserva.CANCELADA_ADMIN)) {

                        System.out.println("Reactivando reserva ID: " + reserva.getId());
                        reserva.setEstadoReserva(Reservas.EstadoReserva.ACTIVA);
                        reserva.setMotivoCancelacion(null); // Limpiar motivo de cancelación
                        reserva.setFechaCancelacion(null); // Limpiar fecha de cancelación
                        reserva.setCanceladoPor(null); // Limpiar quien canceló
                        reservaRepository.save(reserva);
                        System.out.println("Reserva ID " + reserva.getId() + " reactivada exitosamente");
                    }
                    // Si la reserva está activa, mantenerla activa
                    else if (reserva.getEstadoReserva() != null &&
                             reserva.getEstadoReserva().equals(Reservas.EstadoReserva.ACTIVA)) {
                        System.out.println("Reserva ID " + reserva.getId() + " ya está activa, pago aprobado.");
                    }
                    else {
                        System.out.println("Estado de reserva no reconocido: " + reserva.getEstadoReserva());
                    }
                }

                return ResponseEntity.ok("Pago aprobado y reserva reactivada exitosamente");
            } else {
                return ResponseEntity.badRequest().body("Pago no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al aprobar el pago: " + e.getMessage());
        }
    }

    // RECHAZAR PAGO
    @PostMapping("/reservas/rechazar-pago/{id}")
    @ResponseBody
    public ResponseEntity<String> rechazarPago(@PathVariable Integer id,
                                              @RequestParam String motivo,
                                              HttpSession session) {
        try {
            Optional<Pagos> optPago = pagosRepository.findById(id);
            if (optPago.isPresent()) {
                Pagos pago = optPago.get();
                Usuario admin = (Usuario) session.getAttribute("usuario");

                // Actualizar estado del pago
                pago.setEstadoPago(Pagos.EstadoPago.RECHAZADO);
                pago.setFechaVerificacion(new Timestamp(System.currentTimeMillis()));
                pago.setVerificadoPor(admin);
                pago.setObservacionesAdmin(motivo);

                pagosRepository.save(pago);

                // Buscar y cancelar la reserva asociada
                List<Reservas> reservasConPago = reservaRepository.findByPago_Id(id);

                for (Reservas reserva : reservasConPago) {
                    reserva.setEstadoReserva(Reservas.EstadoReserva.CANCELADA_ADMIN);
                    reserva.setMotivoCancelacion("Pago rechazado: " + motivo);
                    reserva.setFechaCancelacion(new Timestamp(System.currentTimeMillis()));
                    reserva.setCanceladoPor(admin);
                    reservaRepository.save(reserva);
                }

                return ResponseEntity.ok("Pago rechazado y reserva cancelada exitosamente");
            } else {
                return ResponseEntity.badRequest().body("Pago no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al rechazar el pago: " + e.getMessage());
        }
    }

    // OBTENER DETALLES DE PAGO
    @GetMapping("/reservas/detalles-pago/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerDetallesPago(@PathVariable Integer id) {
        try {
            Optional<Pagos> optPago = pagosRepository.findById(id);
            if (optPago.isPresent()) {
                Pagos pago = optPago.get();
                Map<String, Object> detalles = new HashMap<>();

                detalles.put("id", pago.getId());
                detalles.put("cantidad", pago.getCantidad());
                detalles.put("estadoPago", pago.getEstadoPago().name());
                detalles.put("fechaPago", pago.getFechaPago());
                detalles.put("numeroTransaccion", pago.getNumeroTransaccion());
                detalles.put("observacionesAdmin", pago.getObservacionesAdmin());

                if (pago.getMedioPago() != null) {
                    detalles.put("medioPago", pago.getMedioPago().getNombre());
                    detalles.put("tipoPago", pago.getMedioPago().getTipoPago().name());
                    detalles.put("datosCuenta", pago.getMedioPago().getDatosCuenta());
                }

                if (pago.getVerificadoPor() != null) {
                    detalles.put("verificadoPor", pago.getVerificadoPor().getNombres() + " " + pago.getVerificadoPor().getApellidos());
                    detalles.put("fechaVerificacion", pago.getFechaVerificacion());
                }

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

                return ResponseEntity.ok(detalles);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Error al obtener detalles del pago: " + e.getMessage()));
        }
    }

    // FORMULARIO PARA NUEVO SERVICIO
    @GetMapping("/nuevo")
    public String nuevoServicio(@ModelAttribute("servicioDTO") ServicioDTO servicioDTO, Model model) {
        // Inicializar EspaciosDeportivos
        EspaciosDeportivos espacio = new EspaciosDeportivos();

        // Inicializar TipoEspacio para evitar errores
        TipoEspacio tipoEspacio = new TipoEspacio();
        espacio.setTipoEspacio(tipoEspacio);

        servicioDTO.setEspacio(espacio);

        // Inicializar Estadios
        Estadios estadios = new Estadios();
        estadios.setUsoPermitido("");
        estadios.setSeguridadDisponible(false);
        estadios.setSonidoPantallasDisponible(false);
        estadios.setIluminacionProfesionalDisponible(false);
        servicioDTO.setEstadios(estadios);

        // Inicializar PistasAtletismo
        PistasAtletismo pistasAtletismo = new PistasAtletismo();
        pistasAtletismo.setImplementos("");
        pistasAtletismo.setLongitud(0.0f);
        servicioDTO.setPista(pistasAtletismo);

        // Inicializar Piscinas
        Piscinas piscinas = new Piscinas();
        piscinas.setRequisitos("");
        piscinas.setClimatizada(false);
        piscinas.setProfundidadMin(0.0f);
        piscinas.setProfundidadMax(0.0f);
        piscinas.setNumCarrilMax(0);
        servicioDTO.setPiscina(piscinas);

        // Inicializar CanchasFutbol
        CanchasFutbol cancha = new CanchasFutbol();
        cancha.setIluminacionNocturna(false);
        cancha.setBalonesDisponibles(false);
        cancha.setAncho(0.0f);
        cancha.setAlto(0.0f);
        servicioDTO.setCancha(cancha);

        model.addAttribute("servicioDTO", servicioDTO);
        model.addAttribute("tipos", tipoEspacioRepository.findAll());
        return "admin/agregarservicio_debug";
    }



    @PutMapping("/actualizar/{id}")
    @ResponseBody
    public ResponseEntity<String> actualizarServicio(@PathVariable("id") Integer id, @RequestBody EspaciosDeportivos espacioActualizado) {
        EspaciosDeportivos espacio = espaciosRepository.findById(id).orElse(null);
        if (espacio == null) {
            return ResponseEntity.notFound().build();
        }

        espacio.setNombre(espacioActualizado.getNombre());
        espacio.setUbicacion(espacioActualizado.getUbicacion());
        espacio.setCorreoContacto(espacioActualizado.getCorreoContacto());
        espacio.setAforo(espacioActualizado.getAforo());
        espacio.setHoraAbre(espacioActualizado.getHoraAbre());
        espacio.setHoraCierra(espacioActualizado.getHoraCierra());

        if (espacioActualizado.getTipoEspacio() != null && espacioActualizado.getTipoEspacio().getNombre() != null) {
            Optional<TipoEspacio> opttipo = tipoEspacioRepository.findById(espacioActualizado.getTipoEspacio().getId());
            if (opttipo.isPresent()) {
                TipoEspacio tipo = opttipo.get();
                espacio.setTipoEspacio(tipo);
            }
        }

        espaciosRepository.save(espacio);
        return ResponseEntity.ok("Actualizado correctamente");
    }


    // GUARDAR NUEVO SERVICIO
    @PostMapping("/guardar")
    public String guardarServicio(@ModelAttribute("espacio") EspaciosDeportivos espacio) {
        espaciosRepository.save(espacio);
        return "redirect:/admin/servicios";
    }



    @DeleteMapping("/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminarServicio(@PathVariable("id") int id) {
        if (!espaciosRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        espaciosRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/guardarservicio")
    public String guardarServicio(@ModelAttribute("servicioDTO") ServicioDTO servicioDTO,
                                  @RequestParam("archivos") MultipartFile[] files,
                                  @RequestParam(value = "latitud", required = false) String latitudStr,
                                  @RequestParam(value = "longitud", required = false) String longitudStr,
                                  @RequestParam(value = "mapsUrl", required = false) String mapsUrl){

        try {
            // Usar el nuevo servicio de imágenes con S3
            ListaFotos listaFotos = imageService.uploadServiceImages(files);
            EspaciosDeportivos espaciosDeportivos = servicioDTO.getEspacio();

            //Validar si la dirección cambió para forzar geocodificación
            if (servicioDTO.getDireccion() != null && !servicioDTO.getDireccion().equals(espaciosDeportivos.getUbicacion())) {
                System.out.println("La dirección ha cambiado. Por favor geocodifica de nuevo.");
                return "admin/agregarservicio_debug"; // Volver al formulario sin guardar
            }

            espaciosDeportivos.setListaFotos(listaFotos);

            // Asegurar que el TipoEspacio esté correctamente configurado
            if(espaciosDeportivos.getTipoEspacio() != null && espaciosDeportivos.getTipoEspacio().getId() != null) {
                TipoEspacio tipoEspacio = tipoEspacioRepository.findById(espaciosDeportivos.getTipoEspacio().getId()).orElse(null);
                espaciosDeportivos.setTipoEspacio(tipoEspacio);
            }

            // Procesar coordenadas de geolocalización
            if (latitudStr != null && !latitudStr.trim().isEmpty() &&
                    longitudStr != null && !longitudStr.trim().isEmpty()) {
                try {
                    BigDecimal latitud = new BigDecimal(latitudStr.trim());
                    BigDecimal longitud = new BigDecimal(longitudStr.trim());

                    // Validar que las coordenadas estén en un rango razonable para Lima
                    if (latitud.compareTo(new BigDecimal("-12.5")) >= 0 &&
                            latitud.compareTo(new BigDecimal("-11.5")) <= 0 &&
                            longitud.compareTo(new BigDecimal("-77.5")) >= 0 &&
                            longitud.compareTo(new BigDecimal("-76.5")) <= 0) {

                        espaciosDeportivos.setLatitud(latitud);
                        espaciosDeportivos.setLongitud(longitud);

                        // Establecer URL del mapa si se proporciona
                        if (mapsUrl != null && !mapsUrl.trim().isEmpty()) {
                            espaciosDeportivos.setMapsUrl(mapsUrl.trim());
                        }

                        System.out.println("Coordenadas guardadas - Lat: " + latitud + ", Lng: " + longitud);
                    } else {
                        System.out.println("Coordenadas fuera del rango válido para Lima - Lat: " + latitud + ", Lng: " + longitud);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error al convertir coordenadas: " + e.getMessage());
                }
            }

            // Establecer operativo como true por defecto
            espaciosDeportivos.setOperativo(true);

            // Verificar que el TipoEspacio no sea null antes de acceder a su ID
            if(espaciosDeportivos.getTipoEspacio() != null && espaciosDeportivos.getTipoEspacio().getId() != null && espaciosDeportivos.getTipoEspacio().getId() == 1){
                Piscinas piscina = servicioDTO.getPiscina();
                espaciosDeportivosRepository.save(espaciosDeportivos);
                piscina.setIdEspacio(espaciosDeportivos.getId());
                piscinaRepository.save(piscina);
                System.out.println("Piscina guardada con ID: " + espaciosDeportivos.getId());
            } else if (espaciosDeportivos.getTipoEspacio() != null && espaciosDeportivos.getTipoEspacio().getId() != null && espaciosDeportivos.getTipoEspacio().getId() == 2) {
                CanchasFutbol canchasFutbol = servicioDTO.getCancha();
                espaciosDeportivosRepository.save(espaciosDeportivos);
                canchasFutbol.setIdEspacio(espaciosDeportivos.getId());
                canchasFutbolRepository.save(canchasFutbol);
                System.out.println("Cancha de fútbol guardada con ID: " + espaciosDeportivos.getId());
            } else if (espaciosDeportivos.getTipoEspacio() != null && espaciosDeportivos.getTipoEspacio().getId() != null && espaciosDeportivos.getTipoEspacio().getId() == 3) {
                PistasAtletismo pistasAtletismo = servicioDTO.getPista();
                espaciosDeportivosRepository.save(espaciosDeportivos);
                pistasAtletismo.setIdEspacio(espaciosDeportivos.getId());
                pistasAtletismoRepository.save(pistasAtletismo);
                System.out.println("Pista de atletismo guardada con ID: " + espaciosDeportivos.getId());
            } else if (espaciosDeportivos.getTipoEspacio() != null && espaciosDeportivos.getTipoEspacio().getId() != null && espaciosDeportivos.getTipoEspacio().getId() == 4) {
                Estadios estadios = servicioDTO.getEstadios();
                espaciosDeportivosRepository.save(espaciosDeportivos);
                estadios.setIdEspacio(espaciosDeportivos.getId());
                estadiosRepository.save(estadios);
                System.out.println("Estadio guardado con ID: " + espaciosDeportivos.getId());
            } else {
                // Si no hay tipo específico, solo guardar el espacio deportivo
                espaciosDeportivosRepository.save(espaciosDeportivos);
                System.out.println("Espacio deportivo guardado con ID: " + espaciosDeportivos.getId());
            }

            // Log de información de geolocalización guardada
            if (espaciosDeportivos.getLatitud() != null && espaciosDeportivos.getLongitud() != null) {
                System.out.println("Servicio guardado con geolocalización:");
                System.out.println("- Nombre: " + espaciosDeportivos.getNombre());
                System.out.println("- Ubicación: " + espaciosDeportivos.getUbicacion());
                System.out.println("- Latitud: " + espaciosDeportivos.getLatitud());
                System.out.println("- Longitud: " + espaciosDeportivos.getLongitud());
                System.out.println("- Maps URL: " + espaciosDeportivos.getMapsUrl());
            }

        } catch (Exception e) {
            System.err.println("Error al guardar servicio: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/nuevo?error=true";
        }
        return "redirect:/admin?success=true";
    }

    // Metodo para actualizar reservas completadas
    private void actualizarReservasCompletadas() {
        try {
            LocalDate hoy = LocalDate.now();
            List<Reservas> reservasActivas = reservaRepository.findAll().stream()
                .filter(r -> r.getEstadoReserva() == Reservas.EstadoReserva.ACTIVA)
                .filter(r -> r.getFechaReserva() != null && r.getFechaReserva().isBefore(hoy))
                .filter(r -> r.getPago() != null && r.getPago().getEstadoPago() == Pagos.EstadoPago.APROBADO)
                .toList();

            for (Reservas reserva : reservasActivas) {
                reserva.setEstadoReserva(Reservas.EstadoReserva.COMPLETADA);
                reservaRepository.save(reserva);
                System.out.println("Reserva ID " + reserva.getId() + " marcada como COMPLETADA");
            }

            if (!reservasActivas.isEmpty()) {
                System.out.println("Se actualizaron " + reservasActivas.size() + " reservas a estado COMPLETADA");
            }
        } catch (Exception e) {
            System.err.println("Error al actualizar reservas completadas: " + e.getMessage());
        }
    }

    //reportes
    @GetMapping("/servicios/exportar-reporte-pdf")
    public void exportarReportePdf(@RequestParam("id") int idEspacio, HttpServletResponse response) throws Exception {
        EspaciosDeportivos espacio = espaciosRepository.findById(idEspacio).orElse(null);
        if (espacio == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Espacio no encontrado");
            return;
        }

        String nombreServicio = espacio.getNombre();

        // Obtener imagen
        byte[] imagen = null;
        if (espacio.getListaFotos() != null) {
            List<Fotos> fotos = fotosRepository.findByListaFotosId(espacio.getListaFotos().getId());
            if (!fotos.isEmpty()) {
                imagen = fotos.get(0).getFoto();
            }
        }

        // Configurar PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_servicio_" + idEspacio + ".pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Logo
        String imagePath = "src/main/resources/static/images/logo-sanMiguel.png";
        ImageData imageData = ImageDataFactory.create(imagePath);
        Image logo = new Image(imageData);
        logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
        logo.setWidth(60);
        document.add(logo);

        // Título
        Paragraph titulo = new Paragraph("Reporte de Servicio Deportivo")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(16);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph(nombreServicio)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(13);
        document.add(subtitulo);

        // Imagen del espacio
        if (imagen != null) {
            Image img = new Image(ImageDataFactory.create(imagen))
                    .scaleToFit(200, 200)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(img);
            document.add(new Paragraph("\n"));
        }

        // Datos del servicio
        document.add(new Paragraph("Tipo: " + espacio.getTipoEspacio().getNombre()));
        document.add(new Paragraph("Ubicación: " + espacio.getUbicacion()));
        document.add(new Paragraph("Horario: " + espacio.getHoraAbre() + " - " + espacio.getHoraCierra()));
        document.add(new Paragraph("Correo: " + espacio.getCorreoContacto()));
        document.add(new Paragraph("\n"));

        // Tabla de reservas
        List<Reservas> reservas = reservaRepository .findByEspacioDeportivoId(idEspacio);
        if (!reservas.isEmpty()) {
            DeviceRgb celesteOscuro = new DeviceRgb(36, 118, 141);

            Table table = new Table(5);
            table.setWidth(UnitValue.createPercentValue(100)); // ✅ Alternativa válida en iText 7

            table.setHorizontalAlignment(HorizontalAlignment.CENTER);

            // Encabezados
            table.addHeaderCell(new Cell().add(new Paragraph("Usuario"))
                    .setBackgroundColor(celesteOscuro)
                    .setFontColor(ColorConstants.WHITE)  // <- Letras blancas
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setPadding(5));

            table.addHeaderCell(new Cell().add(new Paragraph("Fecha"))
                    .setBackgroundColor(celesteOscuro)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setPadding(5));

            table.addHeaderCell(new Cell().add(new Paragraph("Horario"))
                    .setBackgroundColor(celesteOscuro)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setPadding(5));

            table.addHeaderCell(new Cell().add(new Paragraph("Medio Pago"))
                    .setBackgroundColor(celesteOscuro)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setPadding(5));

            table.addHeaderCell(new Cell().add(new Paragraph("Monto"))
                    .setBackgroundColor(celesteOscuro)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setPadding(5));

            // Filas de datos
            for (Reservas r : reservas) {
                table.addCell(new Cell().add(new Paragraph(r.getUsuario().getNombres() + " " + r.getUsuario().getApellidos())).setTextAlignment(TextAlignment.CENTER).setPadding(4));
                table.addCell(new Cell().add(new Paragraph(r.getFechaReserva().toString())).setTextAlignment(TextAlignment.CENTER).setPadding(4));
                table.addCell(new Cell().add(new Paragraph(r.getHorario().getHoraInicio() + " - " + r.getHorario().getHoraFin())).setTextAlignment(TextAlignment.CENTER).setPadding(4));
                table.addCell(new Cell().add(new Paragraph(r.getPago().getMedioPago().getNombre())).setTextAlignment(TextAlignment.CENTER).setPadding(4));
                table.addCell(new Cell().add(new Paragraph("S/ " + r.getPago().getCantidad())).setTextAlignment(TextAlignment.CENTER).setPadding(4));
            }

            document.add(new Paragraph("Reservas realizadas:").setBold());
            document.add(table);
        } else {
            document.add(new Paragraph("No se han registrado reservas para este servicio."));
        }

        document.close();
    }

    @GetMapping("/servicios/exportar-reporte-excel")
    public void exportarReporteExcel(@RequestParam("id") int idEspacio, HttpServletResponse response) throws Exception {
        EspaciosDeportivos espacio = espaciosRepository.findById(idEspacio).orElse(null);
        if (espacio == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Espacio no encontrado");
            return;
        }

        List<Reservas> reservas = reservaRepository.findByEspacioDeportivoId(idEspacio);

        // Crear workbook y hoja
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reservas");

        // Estilo de encabezado
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

        // Crear fila de encabezado
        Row header = sheet.createRow(0);
        String[] columnas = {"Usuario", "Fecha", "Horario", "Medio Pago", "Monto"};

        for (int i = 0; i < columnas.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        // Filas de contenido
        int fila = 1;
        for (Reservas r : reservas) {
            Row dataRow = sheet.createRow(fila++);
            dataRow.createCell(0).setCellValue(r.getUsuario().getNombres() + " " + r.getUsuario().getApellidos());
            dataRow.createCell(1).setCellValue(r.getFechaReserva().toString());
            dataRow.createCell(2).setCellValue(r.getHorario().getHoraInicio() + " - " + r.getHorario().getHoraFin());
            dataRow.createCell(3).setCellValue(r.getPago().getMedioPago().getNombre());
            dataRow.createCell(4).setCellValue("S/ " + r.getPago().getCantidad());
        }

        // Autoajustar columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Configurar descarga
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_servicio_" + idEspacio + ".xlsx");

        // Escribir archivo
        workbook.write(response.getOutputStream());
        workbook.close();
    }



}

