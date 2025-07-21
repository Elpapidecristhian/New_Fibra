package com.example.gtics_ta.Controllers;
import com.example.gtics_ta.DTO.AdminDTO;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.properties.*;
import com.example.gtics_ta.DTO.ServicioDTO;
import com.example.gtics_ta.Entity.*;
import com.example.gtics_ta.Repository.*;
import com.example.gtics_ta.Services.ImageService;
import com.example.gtics_ta.Services.MantenimientoService;
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
import jakarta.transaction.Transactional;
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
    import java.io.InputStream;
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
        @Autowired
        private GimnasiosRepository gimnasiosRepository;
        @Autowired
        private PiscinasRepository piscinasRepository;


        //*********************************************************************************************
        //
        //                                    Dashboard
        //
        //*********************************************************************************************

        // DASHBOARD PRINCIPAL
        @GetMapping(value = {"","/"})
        public String dashboard(Model model) {
            AdminDTO dto = new AdminDTO();
    
            dto.setTotalUsuarios(usuarioRepository.count());
            dto.setTotalUsuariosBaneados(usuarioRepository.countByActivo(false));
            dto.setEspaciosDisponibles(espaciosRepository.countByOperativo(true));
            dto.setCantidadTotalReservas(reservaRepository.contarTotalReservas());
            dto.setCantidadReservasHoy(reservaRepository.contarReservasHoy());

            // Top 10 Servicios Más Reservados
            List<Object[]> topServicios = reservaRepository.top10ServiciosMasReservados();
            List<String> nombresTop = new ArrayList<>();
            List<Long> cantidadesTop = new ArrayList<>();
            for (Object[] fila : topServicios) {
                nombresTop.add((String) fila[0]);
                cantidadesTop.add(((Number) fila[1]).longValue());
            }
            dto.setNombresServiciosTop(nombresTop);
            dto.setCantidadReservasTop(cantidadesTop);

            // Porcentaje Reservas por Servicio
            List<Object[]> porcentajes = reservaRepository.porcentajeReservasPorServicio();
            List<String> nombres = new ArrayList<>();
            List<Long> cantidades = new ArrayList<>();
            for (Object[] fila : porcentajes) {
                nombres.add((String) fila[0]);
                cantidades.add(((Number) fila[1]).longValue());
            }
            dto.setNombresServiciosPorcentaje(nombres);
            dto.setCantidadServiciosPorcentaje(cantidades);

            // Top 10 Usuarios con Más Reservas
            List<Object[]> topUsuarios = reservaRepository.top10UsuariosConMasReservas();
            List<String> nombresUsuarios = new ArrayList<>();
            List<Long> cantidadUsuarios = new ArrayList<>();
            for (Object[] fila : topUsuarios) {
                String nombre = (String) fila[1];
                String apellido = (String) fila[2];
                nombresUsuarios.add(nombre + " " + apellido);
                cantidadUsuarios.add(((Number) fila[3]).longValue());
            }
            dto.setNombresUsuariosTop(nombresUsuarios);
            dto.setCantidadReservasUsuariosTop(cantidadUsuarios);

            // Distribución por Hora
            List<Object[]> porHora = reservaRepository.distribucionReservasPorHora();
            List<String> horas = new ArrayList<>();
            List<Long> cantidadHoras = new ArrayList<>();
            for (Object[] fila : porHora) {
                Integer hora = (Integer) fila[0];
                horas.add(String.format("%02d:00", hora));
                cantidadHoras.add(((Number) fila[1]).longValue());
            }
            dto.setHorasReservas(horas);
            dto.setCantidadReservasPorHora(cantidadHoras);

            model.addAttribute("dashboard", dto);
            return "admin/dashboard";
        }


        // DASHBOARD ALTERNATIVO
        @GetMapping("/dashboard")
        public String mostrarDashboard(Model model) {
            return dashboard(model);
        }

        //*********************************************************************************************
        //
        //                                    Servicios
        //
        //*********************************************************************************************

        // LISTAR SERVICIOS
        @GetMapping("/servicios")
        public String listarServicios(Model model) {
            List<EspaciosDeportivos> espacios = espaciosRepository.findAll();
            model.addAttribute("listaEspacios", espacios);
            return "admin/servicios";
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

            //Inicializar Gimnasios
            Gimnasios gimnasio = new Gimnasios();
            gimnasio.setCantidadMaquinas(0);
            gimnasio.setTiposMaquinas("");
            gimnasio.setCostoAnual(0.0f);
            gimnasio.setCostoMensual(0.0f);
            gimnasio.setCostoSemanal(0.0f);
            gimnasio.setTieneDuchas(false);
            gimnasio.setTieneSauna(false);
            servicioDTO.setGimnasios(gimnasio);

            model.addAttribute("servicioDTO", servicioDTO);
            model.addAttribute("tipos", tipoEspacioRepository.findAll());
            return "admin/agregarservicio_debug";
        }

        @GetMapping("/editar")
        public String editarServicio(@ModelAttribute("servicioDTO") ServicioDTO servicioDTO, @RequestParam("id") Integer idEspacio, Model model){
            Optional<EspaciosDeportivos> optEspacio = espaciosRepository.findById(idEspacio);
            if (optEspacio.isPresent()) {
                EspaciosDeportivos espacio = optEspacio.get();
                espacio.setHoraAbre(espacio.getHoraAbre().withSecond(0).withNano(0));
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

                //Inicializar Gimnasios
                Gimnasios gimnasio = new Gimnasios();
                gimnasio.setCantidadMaquinas(0);
                gimnasio.setTiposMaquinas("");
                gimnasio.setCostoAnual(0.0f);
                gimnasio.setCostoMensual(0.0f);
                gimnasio.setCostoSemanal(0.0f);
                gimnasio.setTieneDuchas(false);
                gimnasio.setTieneSauna(false);

                servicioDTO.setGimnasios(gimnasio);
                switch (espacio.getTipoEspacio().getId()){
                    case 1:
                        servicioDTO.setPiscina(piscinaRepository.findByIdEspacio(espacio.getId()));
                        break;
                    case 2:
                        servicioDTO.setCancha(canchasFutbolRepository.findByIdEspacio(espacio.getId()));
                        break;
                    case 3:
                        servicioDTO.setPista(pistasAtletismoRepository.findByIdEspacio(espacio.getId()));
                        break;
                    case 4:
                        servicioDTO.setEstadios(estadiosRepository.findByIdEspacio(espacio.getId()));
                        break;
                    case 5:
                        servicioDTO.setGimnasios(gimnasiosRepository.findByIdEspacios(espacio.getId()));
                        break;
                    default:
                        return "redirect:/admin/servicios";
                }
                model.addAttribute("servicioDTO", servicioDTO);
                model.addAttribute("tipos", tipoEspacioRepository.findAll());
                return "admin/editarservicio";
            }
            return "redirect:/admin/servicios";
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
                return "redirect:/admin";
            }
            return "redirect:/admin";
        }

        @Transactional
        @PostMapping("/eliminar/")
        public String eliminarServicio(@RequestParam("id") int id) {
            Optional<EspaciosDeportivos> optEspacio = espaciosRepository.findById(id);
            if (optEspacio.isPresent()) {
                EspaciosDeportivos espacio = optEspacio.get();
                switch (espacio.getTipoEspacio().getId()){
                    case 1:
                        Piscinas piscinas = piscinaRepository.findByIdEspacio(espacio.getId());
                        piscinasRepository.delete(piscinas);
                        break;
                    case 2:
                        CanchasFutbol canchasFutbol = canchasFutbolRepository.findByIdEspacio(espacio.getId());
                        canchasFutbolRepository.delete(canchasFutbol);
                        break;
                    case 3:
                        PistasAtletismo pistasAtletismo = pistasAtletismoRepository.findByIdEspacio(espacio.getId());
                        pistasAtletismoRepository.delete(pistasAtletismo);
                        break;
                    case 4:
                        Estadios estadios = estadiosRepository.findByIdEspacio(espacio.getId());
                        estadiosRepository.delete(estadios);
                        break;
                    case 5:
                        Gimnasios gimnasios = gimnasiosRepository.findByIdEspacios(espacio.getId());
                        gimnasiosRepository.delete(gimnasios);
                        break;
                }
                List<Horarios> listaHorarios = horariosRepository.findByEspacioId(espacio.getId());
                for(Horarios h : listaHorarios){
                    reservaRepository.deleteAllByHorario(h);
                }
                for(Horarios h : listaHorarios){
                    horarioReservadoRepository.deleteAllByHorario(h);
                }
                horariosRepository.deleteAll(listaHorarios);
                espaciosRepository.delete(espacio);
            } else {
                return "redirect:/admin";
            }
            return "redirect:/admin";
        }

        //*********************************************************************************************
        //
        //                                    Reservas
        //
        //*********************************************************************************************

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

        //*********************************************************************************************
        //
        //                                     Reportes
        //
        //*********************************************************************************************

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
            // ✅ Esta versión funciona tanto en local como en la nube
            InputStream imageStream = getClass().getResourceAsStream("/static/images/logo-sanMiguel.png");
            if (imageStream != null) {
                byte[] imageBytes = imageStream.readAllBytes();
                ImageData imageData = ImageDataFactory.create(imageBytes);
                Image logo = new Image(imageData);
                logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
                logo.setWidth(60);
                document.add(logo);
            } else {
                System.err.println("⚠️ No se pudo cargar el logo desde /static/images/logo-sanMiguel.png");
            }


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

    // ==================== ENDPOINTS DE MANTENIMIENTO ====================

    /**
     * Programa un nuevo mantenimiento
     */
    @PostMapping("/programar-mantenimiento")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> programarMantenimiento(
            @RequestParam("servicioId") Integer servicioId,
            @RequestParam("fecha") String fecha,
            @RequestParam("tipo") String tipo,
            @RequestParam("horaInicio") String horaInicio,
            @RequestParam("horaFin") String horaFin,
            @RequestParam("responsable") String responsable,
            @RequestParam("contactoEncargado") String contactoEncargado,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("prioridad") String prioridad,
            @RequestParam(value = "suspenderServicio", defaultValue = "false") boolean suspenderServicio,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("=== INICIANDO PROGRAMACIÓN DE MANTENIMIENTO ===");
            System.out.println("Servicio ID: " + servicioId);
            System.out.println("Fecha: " + fecha);
            System.out.println("Tipo: " + tipo);
            System.out.println("Hora inicio: " + horaInicio);
            System.out.println("Hora fin: " + horaFin);
            System.out.println("Suspender servicio: " + suspenderServicio);

            // Obtener usuario de la sesión
            Usuario admin = (Usuario) session.getAttribute("usuario");
            if (admin == null) {
                System.out.println("ERROR: Sesión expirada");
                response.put("success", false);
                response.put("error", "Sesión expirada");
                return ResponseEntity.status(401).body(response);
            }
            System.out.println("Admin: " + admin.getNombres() + " " + admin.getApellidos());

            // Validar y convertir datos
            LocalDate fechaMantenimiento = LocalDate.parse(fecha);
            LocalTime horaInicioTime = LocalTime.parse(horaInicio);
            LocalTime horaFinTime = LocalTime.parse(horaFin);

            // Validaciones
            if (fechaMantenimiento.isBefore(LocalDate.now())) {
                System.out.println("ERROR: Fecha anterior a hoy");
                response.put("success", false);
                response.put("error", "La fecha del mantenimiento no puede ser anterior a hoy");
                return ResponseEntity.badRequest().body(response);
            }

            if (horaInicioTime.isAfter(horaFinTime) || horaInicioTime.equals(horaFinTime)) {
                System.out.println("ERROR: Horarios inválidos");
                response.put("success", false);
                response.put("error", "La hora de fin debe ser posterior a la hora de inicio");
                return ResponseEntity.badRequest().body(response);
            }

            System.out.println("Validaciones pasadas, llamando al servicio...");

            // Programar el mantenimiento
            Mantenimiento mantenimiento = mantenimientoService.programarMantenimiento(
                    servicioId, tipo, fechaMantenimiento, horaInicioTime, horaFinTime,
                    responsable, contactoEncargado, descripcion, prioridad, suspenderServicio, admin
            );

            System.out.println("Mantenimiento creado con ID: " + mantenimiento.getId());
            System.out.println("Reservas canceladas: " + mantenimiento.getReservasCanceladas());
            System.out.println("Notificaciones enviadas: " + mantenimiento.getNotificacionesEnviadas());

            // Respuesta exitosa
            response.put("success", true);
            response.put("message", "Mantenimiento programado exitosamente");
            response.put("mantenimientoId", mantenimiento.getId());
            response.put("reservasCanceladas", mantenimiento.getReservasCanceladas());
            response.put("notificacionesEnviadas", mantenimiento.getNotificacionesEnviadas());

            System.out.println("=== MANTENIMIENTO PROGRAMADO EXITOSAMENTE ===");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("ERROR COMPLETO: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("error", "Error al programar mantenimiento: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    //*********************************************************************************************
    //
    //                                    Observaciones
    //
    //*********************************************************************************************

    /**
     * Página principal de observaciones de coordinadores
     */
    @GetMapping("/observaciones")
    public String observaciones(Model model, HttpSession session) {
        // Verificar sesión de admin
        Usuario admin = (Usuario) session.getAttribute("usuario");
        if (admin == null) {
            return "redirect:/login";
        }

        try {
            // Obtener todas las observaciones ordenadas por fecha (más recientes primero)
            List<Comentarios> todasLasObservaciones = comentariosRepository.findAllByOrderByFechaCreacionDesc();

            if (todasLasObservaciones == null) {
                todasLasObservaciones = new ArrayList<>();
            }

            // Filtrar solo las observaciones de coordinadores (usuarios con rol COORDINADOR)
            List<Comentarios> observacionesCoordinadores = todasLasObservaciones.stream()
                    .filter(comentario -> comentario != null &&
                            comentario.getUsuario() != null &&
                            comentario.getUsuario().getRol() != null &&
                            "COORDINADOR".equalsIgnoreCase(comentario.getUsuario().getRol().getNombre()))
                    .toList();


            model.addAttribute("comentarios", observacionesCoordinadores);

            // Estadísticas adicionales
            long totalReparaciones = observacionesCoordinadores.stream()
                    .filter(c -> c.getTipoComentario() == Comentarios.TipoComentario.REPARACION)
                    .count();

            long totalObservaciones = observacionesCoordinadores.stream()
                    .filter(c -> c.getTipoComentario() == Comentarios.TipoComentario.COMENTARIO)
                    .count();

            long observacionesAlta = observacionesCoordinadores.stream()
                    .filter(c -> c.getPrioridadUsuario() == Comentarios.PrioridadUsuario.ALTA)
                    .count();

            long observacionesNoRevisadas = observacionesCoordinadores.stream()
                    .filter(c -> !c.getRevisadoPorAdmin())
                    .count();

            model.addAttribute("totalReparaciones", totalReparaciones);
            model.addAttribute("totalObservaciones", totalObservaciones);
            model.addAttribute("observacionesAlta", observacionesAlta);
            model.addAttribute("observacionesNoRevisadas", observacionesNoRevisadas);

            System.out.println("=== OBSERVACIONES CARGADAS ===");
            System.out.println("Total observaciones de coordinadores: " + observacionesCoordinadores.size());
            System.out.println("Reparaciones: " + totalReparaciones);
            System.out.println("Observaciones generales: " + totalObservaciones);
            System.out.println("Prioridad alta: " + observacionesAlta);
            System.out.println("No revisadas: " + observacionesNoRevisadas);

            return "admin/observaciones";

        } catch (Exception e) {
            System.out.println("ERROR al cargar observaciones: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("comentarios", new ArrayList<>());
            model.addAttribute("error", "Error al cargar las observaciones: " + e.getMessage());
            return "admin/observaciones";
        }
    }

    //*********************************************************************************************
    //
    //                                Asignación de Horarios
    //
    //*********************************************************************************************

    /**
     * Página principal de asignación de horarios a coordinadores
     */
    @GetMapping("/asignar-horarios")
    public String asignarHorarios(Model model, HttpSession session) {
        // Verificar sesión de admin
        Usuario admin = (Usuario) session.getAttribute("usuario");
        if (admin == null) {
            return "redirect:/login";
        }

        try {
            // Obtener lista de coordinadores activos
            List<Usuario> coordinadores = usuarioRepository.findByRol_NombreAndActivo("COORDINADOR", true);
            model.addAttribute("coordinadores", coordinadores);

            // Obtener lista de espacios deportivos operativos
            List<EspaciosDeportivos> espacios = espaciosRepository.findByOperativo(true);
            model.addAttribute("espacios", espacios);

            System.out.println("=== ASIGNACIÓN DE HORARIOS ===");
            System.out.println("Coordinadores disponibles: " + coordinadores.size());
            System.out.println("Espacios disponibles: " + espacios.size());

            return "admin/asignar-horarios";

        } catch (Exception e) {
            System.out.println("ERROR al cargar página de asignación de horarios: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar la página: " + e.getMessage());
            return "admin/asignar-horarios";
        }
    }

    /**
     * Verificar conflictos de horarios para un coordinador
     */
    @PostMapping("/verificar-conflicto-horario")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verificarConflictoHorario(
            @RequestParam("coordinadorId") Integer coordinadorId,
            @RequestParam("fecha") String fecha,
            @RequestParam("horaInicio") String horaInicio,
            @RequestParam("horaFin") String horaFin,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Verificar sesión
            Usuario admin = (Usuario) session.getAttribute("usuario");
            if (admin == null) {
                response.put("conflicto", true);
                response.put("mensaje", "Sesión expirada");
                return ResponseEntity.status(401).body(response);
            }

            // Obtener coordinador
            Optional<Usuario> coordinadorOpt = usuarioRepository.findById(coordinadorId);
            if (!coordinadorOpt.isPresent()) {
                response.put("conflicto", true);
                response.put("mensaje", "Coordinador no encontrado");
                return ResponseEntity.badRequest().body(response);
            }

            Usuario coordinador = coordinadorOpt.get();

            // Convertir fecha y horas
            LocalDate fechaAsignacion = LocalDate.parse(fecha);
            LocalTime horaInicioTime = LocalTime.parse(horaInicio);
            LocalTime horaFinTime = LocalTime.parse(horaFin);

            // Validar que la hora de fin sea posterior a la de inicio
            if (horaInicioTime.isAfter(horaFinTime) || horaInicioTime.equals(horaFinTime)) {
                response.put("conflicto", true);
                response.put("mensaje", "La hora de fin debe ser posterior a la hora de inicio");
                return ResponseEntity.ok(response);
            }

            // Buscar horarios existentes del coordinador para esa fecha
            Date fechaJava = java.sql.Date.valueOf(fechaAsignacion);
            List<HorariosCoordinador> horariosExistentes = horariosCoordinadorRepository.findByUsuarioAndFecha(coordinador, fechaJava);

            // Verificar conflictos de horario
            for (HorariosCoordinador horarioExistente : horariosExistentes) {
                LocalTime horaInicioExistente = horarioExistente.getHoraEntrada().toLocalTime();
                LocalTime horaFinExistente = horarioExistente.getHoraSalida().toLocalTime();

                // Verificar solapamiento
                boolean hayConflicto = !(horaFinTime.isBefore(horaInicioExistente) ||
                        horaInicioTime.isAfter(horaFinExistente));

                if (hayConflicto) {
                    response.put("conflicto", true);
                    response.put("mensaje", String.format(
                            "Conflicto con horario existente en %s de %s a %s",
                            horarioExistente.getEspacio().getNombre(),
                            horaInicioExistente.toString(),
                            horaFinExistente.toString()
                    ));
                    return ResponseEntity.ok(response);
                }
            }

            // No hay conflictos
            response.put("conflicto", false);
            response.put("mensaje", "Horario disponible");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("ERROR verificando conflictos: " + e.getMessage());
            e.printStackTrace();
            response.put("conflicto", true);
            response.put("mensaje", "Error al verificar conflictos: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Asignar horario a un coordinador
     */
    @PostMapping("/asignar-horario")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> asignarHorario(
            @RequestParam("coordinadorId") Integer coordinadorId,
            @RequestParam("espacioId") Integer espacioId,
            @RequestParam("horaInicio") String horaInicio,
            @RequestParam("horaFin") String horaFin,
            @RequestParam("diaSeleccionado") Integer diaSeleccionado,
            @RequestParam("fechaAsignacion") String fechaAsignacion,
            @RequestParam(value = "observaciones", required = false) String observaciones,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("=== ASIGNANDO HORARIO ===");
            System.out.println("Coordinador ID: " + coordinadorId);
            System.out.println("Espacio ID: " + espacioId);
            System.out.println("Fecha: " + fechaAsignacion);
            System.out.println("Hora: " + horaInicio + " - " + horaFin);

            // Verificar sesión
            Usuario admin = (Usuario) session.getAttribute("usuario");
            if (admin == null) {
                response.put("success", false);
                response.put("error", "Sesión expirada");
                return ResponseEntity.status(401).body(response);
            }

            // Obtener coordinador
            Optional<Usuario> coordinadorOpt = usuarioRepository.findById(coordinadorId);
            if (!coordinadorOpt.isPresent()) {
                response.put("success", false);
                response.put("error", "Coordinador no encontrado");
                return ResponseEntity.badRequest().body(response);
            }

            // Obtener espacio deportivo
            Optional<EspaciosDeportivos> espacioOpt = espaciosRepository.findById(espacioId);
            if (!espacioOpt.isPresent()) {
                response.put("success", false);
                response.put("error", "Espacio deportivo no encontrado");
                return ResponseEntity.badRequest().body(response);
            }

            Usuario coordinador = coordinadorOpt.get();
            EspaciosDeportivos espacio = espacioOpt.get();

            // Convertir fecha y horas
            LocalDate fechaAsignacionLocal = LocalDate.parse(fechaAsignacion);
            LocalTime horaInicioTime = LocalTime.parse(horaInicio);
            LocalTime horaFinTime = LocalTime.parse(horaFin);

            // Validaciones
            if (horaInicioTime.isAfter(horaFinTime) || horaInicioTime.equals(horaFinTime)) {
                response.put("success", false);
                response.put("error", "La hora de fin debe ser posterior a la hora de inicio");
                return ResponseEntity.badRequest().body(response);
            }

            // Verificar conflictos una vez más
            Date fechaJava = java.sql.Date.valueOf(fechaAsignacionLocal);
            List<HorariosCoordinador> horariosExistentes = horariosCoordinadorRepository.findByUsuarioAndFecha(coordinador, fechaJava);

            for (HorariosCoordinador horarioExistente : horariosExistentes) {
                LocalTime horaInicioExistente = horarioExistente.getHoraEntrada().toLocalTime();
                LocalTime horaFinExistente = horarioExistente.getHoraSalida().toLocalTime();

                boolean hayConflicto = !(horaFinTime.isBefore(horaInicioExistente) ||
                        horaInicioTime.isAfter(horaFinExistente));

                if (hayConflicto) {
                    response.put("success", false);
                    response.put("error", "Conflicto con horario existente en " + horarioExistente.getEspacio().getNombre());
                    return ResponseEntity.badRequest().body(response);
                }
            }

            // Crear nuevo horario
            HorariosCoordinador nuevoHorario = new HorariosCoordinador();
            nuevoHorario.setUsuario(coordinador);
            nuevoHorario.setEspacio(espacio);
            nuevoHorario.setHoraEntrada(Time.valueOf(horaInicioTime));
            nuevoHorario.setHoraSalida(Time.valueOf(horaFinTime));
            nuevoHorario.setFechaInicio(fechaJava);
            nuevoHorario.setFechaFin(fechaJava); // Para horarios de un solo día

            // Guardar en base de datos
            horariosCoordinadorRepository.save(nuevoHorario);

            System.out.println("Horario asignado exitosamente con ID: " + nuevoHorario.getId());

            response.put("success", true);
            response.put("message", "Horario asignado exitosamente");
            response.put("horarioId", nuevoHorario.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("ERROR asignando horario: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("error", "Error al asignar horario: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Obtener horarios de un coordinador para una semana específica o todos los horarios
     */
    @GetMapping("/horarios-coordinador")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerHorariosCoordinador(
            @RequestParam("coordinadorId") Integer coordinadorId,
            @RequestParam(value = "fechaInicio", required = false) String fechaInicio,
            @RequestParam(value = "fechaFin", required = false) String fechaFin,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Verificar sesión
            Usuario admin = (Usuario) session.getAttribute("usuario");
            if (admin == null) {
                response.put("success", false);
                response.put("error", "Sesión expirada");
                return ResponseEntity.status(401).body(response);
            }

            // Obtener coordinador
            Optional<Usuario> coordinadorOpt = usuarioRepository.findById(coordinadorId);
            if (!coordinadorOpt.isPresent()) {
                response.put("success", false);
                response.put("error", "Coordinador no encontrado");
                return ResponseEntity.badRequest().body(response);
            }

            Usuario coordinador = coordinadorOpt.get();

            List<HorariosCoordinador> horarios;

            if (fechaInicio == null || fechaFin == null) {
                horarios = horariosCoordinadorRepository.findByUsuario(coordinador);
                System.out.println("Obteniendo TODOS los horarios del coordinador: " + coordinador.getNombres());
            } else {
                // Convertir fechas
                LocalDate fechaInicioLocal = LocalDate.parse(fechaInicio);
                LocalDate fechaFinLocal = LocalDate.parse(fechaFin);
                Date fechaInicioJava = java.sql.Date.valueOf(fechaInicioLocal);
                Date fechaFinJava = java.sql.Date.valueOf(fechaFinLocal);

                horarios = horariosCoordinadorRepository.findByUsuarioAndFechaInicioBetween(
                        coordinador, fechaInicioJava, fechaFinJava);

                System.out.println("=== OBTENIENDO HORARIOS COORDINADOR ===");
                System.out.println("Coordinador: " + coordinador.getNombres() + " " + coordinador.getApellidos());
                System.out.println("Rango de fechas: " + fechaInicioJava + " a " + fechaFinJava);
                System.out.println("Horarios encontrados: " + horarios.size());
            }

            // Convertir a formato para el calendario
            List<Map<String, Object>> horariosCalendario = new ArrayList<>();
            for (HorariosCoordinador horario : horarios) {
                Map<String, Object> evento = new HashMap<>();
                evento.put("id", horario.getId());
                evento.put("espacio", horario.getEspacio().getNombre());
                evento.put("fechaInicio", horario.getFechaInicio().toString());
                evento.put("fechaFin", horario.getFechaFin().toString());
                evento.put("horaInicio", horario.getHoraEntrada().toString());
                evento.put("horaFin", horario.getHoraSalida().toString());
                horariosCalendario.add(evento);

                System.out.println("Horario: " + horario.getEspacio().getNombre() +
                        " - " + horario.getFechaInicio() +
                        " de " + horario.getHoraEntrada() + " a " + horario.getHoraSalida());
            }

            response.put("success", true);
            response.put("horarios", horariosCalendario);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("ERROR obteniendo horarios: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("error", "Error al obtener horarios: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

}

