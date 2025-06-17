package com.example.gtics_ta.Controllers;

import com.example.gtics_ta.DTO.AdminDTO;
import com.example.gtics_ta.DTO.ServicioDTO;
import com.example.gtics_ta.Entity.*;
import com.example.gtics_ta.Repository.*;
import com.example.gtics_ta.Services.ImageService;

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

    // Método para actualizar reservas completadas
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

}

