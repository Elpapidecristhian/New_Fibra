package com.example.gtics_ta.Controllers;

import com.example.gtics_ta.DTO.AdminDTO;
import com.example.gtics_ta.DTO.ServicioDTO;
import com.example.gtics_ta.Entity.*;
import com.example.gtics_ta.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
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
                if (pago.getListaFotosComprobantes() != null) {
                    detalles.put("tieneComprobantes", true);
                    // Aquí podrías agregar las URLs de las fotos si es necesario
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
    public String guardarServicio(@ModelAttribute("servicioDTO") ServicioDTO servicioDTO, @RequestParam("archivos") MultipartFile[] files ){
        // Validar que se hayan subido archivos
        if(files == null || files.length == 0 || files[0].isEmpty()) {
            return "admin/agregarservicio_debug";
        }

        // Validar máximo 4 imágenes
        if(files.length > 4) {
            return "admin/agregarservicio_debug";
        }

        try {
            // Crear lista de fotos
            ListaFotos listaFotos = new ListaFotos();
            listaFotosRepository.save(listaFotos);

            // Procesar cada archivo
            for(MultipartFile file : files) {
                if(!file.isEmpty()) {
                    String fileName = file.getOriginalFilename();

                    // Validar nombre de archivo
                    if (fileName.contains("..")) {
                        continue; // Saltar archivo inválido
                    }

                    // Validar tamaño (5MB máximo)
                    if (file.getSize() > 5 * 1024 * 1024) {
                        continue; // Saltar archivo muy grande
                    }

                    // Validar tipo de archivo
                    String contentType = file.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        continue; // Saltar archivo que no es imagen
                    }

                    // Crear y guardar foto
                    Fotos foto = new Fotos();
                    foto.setFoto(file.getBytes());
                    foto.setFotoNombre(fileName);
                    foto.setFotoTipoArchivo(contentType);
                    foto.setListaFotos(listaFotos);
                    fotosRepository.save(foto);
                }
            }
            EspaciosDeportivos espaciosDeportivos = servicioDTO.getEspacio();
            espaciosDeportivos.setListaFotos(listaFotos);

            // Asegurar que el TipoEspacio esté correctamente configurado
            if(espaciosDeportivos.getTipoEspacio() != null && espaciosDeportivos.getTipoEspacio().getId() != null) {
                TipoEspacio tipoEspacio = tipoEspacioRepository.findById(espaciosDeportivos.getTipoEspacio().getId()).orElse(null);
                espaciosDeportivos.setTipoEspacio(tipoEspacio);
            }

            // Establecer operativo como true por defecto
            espaciosDeportivos.setOperativo(true);

            // Verificar que el TipoEspacio no sea null antes de acceder a su ID
            if(espaciosDeportivos.getTipoEspacio() != null && espaciosDeportivos.getTipoEspacio().getId() != null && espaciosDeportivos.getTipoEspacio().getId() == 1){
                Piscinas piscina = servicioDTO.getPiscina();
                espaciosDeportivosRepository.save(espaciosDeportivos);
                piscina.setIdEspacio(espaciosDeportivos.getId());
                piscinaRepository.save(piscina);
            } else if (espaciosDeportivos.getTipoEspacio() != null && espaciosDeportivos.getTipoEspacio().getId() != null && espaciosDeportivos.getTipoEspacio().getId() == 2) {
                CanchasFutbol canchasFutbol = servicioDTO.getCancha();
                espaciosDeportivosRepository.save(espaciosDeportivos);
                canchasFutbol.setIdEspacio(espaciosDeportivos.getId());
                canchasFutbolRepository.save(canchasFutbol);
            } else if (espaciosDeportivos.getTipoEspacio() != null && espaciosDeportivos.getTipoEspacio().getId() != null && espaciosDeportivos.getTipoEspacio().getId() == 3) {
                PistasAtletismo pistasAtletismo = servicioDTO.getPista();
                espaciosDeportivosRepository.save(espaciosDeportivos);
                pistasAtletismo.setIdEspacio(espaciosDeportivos.getId());
                pistasAtletismoRepository.save(pistasAtletismo);
            } else if (espaciosDeportivos.getTipoEspacio() != null && espaciosDeportivos.getTipoEspacio().getId() != null && espaciosDeportivos.getTipoEspacio().getId() == 4) {
                Estadios estadios = servicioDTO.getEstadios();
                espaciosDeportivosRepository.save(espaciosDeportivos);
                estadios.setIdEspacio(espaciosDeportivos.getId());
                estadiosRepository.save(estadios);
            } else {
                // Si no hay tipo específico, solo guardar el espacio deportivo
                espaciosDeportivosRepository.save(espaciosDeportivos);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin";
        }
        return "redirect:/admin";
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

