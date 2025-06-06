package com.example.gtics_ta.Controllers;

import com.example.gtics_ta.DTO.AdminDTO;
import com.example.gtics_ta.DTO.ServicioDTO;
import com.example.gtics_ta.Entity.*;
import com.example.gtics_ta.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    public String listarReservas(@RequestParam(value = "nombre", required = false) String nombre, Model model) {
        try {
            List<Reservas> reservas;
            if (nombre == null || nombre.isEmpty()) {
                reservas = reservaRepository.findAll();
            } else {
                reservas = reservaRepository.findByEspacioDeportivo_NombreContainingIgnoreCase(nombre);
            }
            model.addAttribute("listaReservas", reservas);
            System.out.println("Número de reservas encontradas: " + reservas.size());
        } catch (Exception e) {
            System.err.println("Error al cargar reservas: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("listaReservas", Collections.emptyList());
        }
        return "admin/reservas";
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
        estadios.setUsoPermitido("-");
        servicioDTO.setEstadios(estadios);

        // Inicializar PistasAtletismo
        PistasAtletismo pistasAtletismo = new PistasAtletismo();
        pistasAtletismo.setImplementos("-");
        servicioDTO.setPista(pistasAtletismo);

        // Inicializar Piscinas
        Piscinas piscinas = new Piscinas();
        piscinas.setRequisitos("-");
        servicioDTO.setPiscina(piscinas);

        // Inicializar CanchasFutbol
        CanchasFutbol cancha = new CanchasFutbol();
        servicioDTO.setCancha(cancha);

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

            if(espaciosDeportivos.getTipoEspacio().getId() == 1){
                Piscinas piscina = servicioDTO.getPiscina();
                espaciosDeportivosRepository.save(espaciosDeportivos);
                piscina.setIdEspacio(espaciosDeportivos.getId());
                piscinaRepository.save(piscina);
            } else if (espaciosDeportivos.getTipoEspacio().getId() == 2) {
                CanchasFutbol canchasFutbol = servicioDTO.getCancha();
                espaciosDeportivosRepository.save(espaciosDeportivos);
                canchasFutbol.setIdEspacio(espaciosDeportivos.getId());
                canchasFutbolRepository.save(canchasFutbol);
            } else if (espaciosDeportivos.getTipoEspacio().getId() == 3) {
                PistasAtletismo pistasAtletismo = servicioDTO.getPista();
                espaciosDeportivosRepository.save(espaciosDeportivos);
                pistasAtletismo.setIdEspacio(espaciosDeportivos.getId());
                pistasAtletismoRepository.save(pistasAtletismo);
            } else if (espaciosDeportivos.getTipoEspacio().getId() == 4) {
                Estadios estadios = servicioDTO.getEstadios();
                espaciosDeportivosRepository.save(espaciosDeportivos);
                estadios.setIdEspacio(espaciosDeportivos.getId());
                estadiosRepository.save(estadios);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin";
        }
        return "redirect:/admin";
    }
}

