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
    private ReservaRepository reservaRepository;
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


    // LISTAR TODOS
    @GetMapping(value = "/servicios")
    public String listarServicios(Model model) {
        List<EspaciosDeportivos> espacios = espaciosRepository.findAll();
        model.addAttribute("listaEspacios", espacios);
        return "admin/servicios"; // Debes tener este archivo .html
    }

    @GetMapping(value={"", "/"})
    public String Dashboard(Model model) {
        AdminDTO dto = new AdminDTO();

        dto.setTotalUsuarios(usuarioRepository.count());
        dto.setTotalUsuariosBaneados(usuarioRepository.countByActivo(false));
        dto.setEspaciosDisponibles(espaciosDeportivosRepository.countByOperativo(true));
        dto.setCantidadTotalReservas(reservaRepository.contarTotalReservas());
        dto.setCantidadReservasHoy(reservaRepository.contarReservasHoy());

        List<Object[]> topServicios = reservaRepository.top10ServiciosMasReservados();
        List<String> nombresTop = new ArrayList<>();
        List<Long> cantidadesTop = new ArrayList<>();

        for (Object[] fila : topServicios) {
            nombresTop.add((String) fila[0]);
            cantidadesTop.add(((Number) fila[1]).longValue());
        }

        dto.setNombresServiciosTop(nombresTop);
        dto.setCantidadReservasTop(cantidadesTop);

        List<Object[]> porcentajes = reservaRepository.porcentajeReservasPorServicio();
        List<String> nombres = new ArrayList<>();
        List<Long> cantidades = new ArrayList<>();

        for (Object[] fila : porcentajes) {
            nombres.add((String) fila[0]);
            cantidades.add(((Number) fila[1]).longValue());
        }
// TOP 10 USUARIOS
        List<Object[]> topUsuarios = reservaRepository.top10UsuariosConMasReservas();
        List<String> nombresUsuarios = new ArrayList<>();
        List<Long> cantidadUsuarios = new ArrayList<>();

        int count = 0;
        for (Object[] fila : topUsuarios) {
            if (count++ >= 10) break;

            // Asegúrate de castear correctamente cada campo
            String nombrePersona = (String) fila[1];
            String apellidoPersona = (String) fila[2];
            String nombreCompleto = nombrePersona + " " + apellidoPersona;


            nombresUsuarios.add(nombreCompleto);
            cantidadUsuarios.add(((Number) fila[3]).longValue());
        }
        dto.setNombresUsuariosTop(nombresUsuarios);
        dto.setCantidadReservasUsuariosTop(cantidadUsuarios);


// RESERVAS POR HORA
        List<Object[]> porHora = reservaRepository.distribucionReservasPorHora();
        List<String> horas = new ArrayList<>();
        List<Long> cantidadHoras = new ArrayList<>();
        for (Object[] fila : porHora) {
            Integer horaInt = (Integer) fila[0];
            horas.add(String.format("%02d:00", horaInt));  // ej: "08:00"
            cantidadHoras.add(((Number) fila[1]).longValue());
        }
        dto.setHorasReservas(horas);
        dto.setCantidadReservasPorHora(cantidadHoras);

        dto.setNombresServiciosPorcentaje(nombres);
        dto.setCantidadServiciosPorcentaje(cantidades);

        model.addAttribute("dashboard", dto);

        return "admin/dashboard"; // Vista correspondiente
    }

    // FORMULARIO PARA NUEVO
    @GetMapping("/nuevo")
    public String nuevoServicio(@ModelAttribute("servicioDTO") ServicioDTO servicioDTO, Model model) {
        Estadios estadios = new Estadios();
        estadios.setUsoPermitido("-");
        PistasAtletismo pistasAtletismo = new PistasAtletismo();
        pistasAtletismo.setImplementos("-");
        Piscinas piscinas = new Piscinas();
        piscinas.setRequisitos("-");
        servicioDTO.setEstadios(estadios);
        servicioDTO.setPista(pistasAtletismo);
        servicioDTO.setPiscina(piscinas);
        model.addAttribute("tipos", tipoEspacioRepository.findAll());
        return "admin/agregarservicio"; // El form para crear/editar
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


    // GUARDAR NUEVO O EDITADO
    @PostMapping("/guardar")
    public String guardarServicio(@ModelAttribute("espacio") EspaciosDeportivos espacio) {
        espaciosRepository.save(espacio);
        return "redirect:admin/servicios";
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
    public String guardarServicio(@ModelAttribute("servicioDTO") ServicioDTO servicioDTO, @RequestParam("archivo") MultipartFile file ){
        if(file.isEmpty()) {
            return "admin/agregarservicio";
        }

        String fileName = file.getOriginalFilename();

        if (fileName.contains("..")){
            return "admin/agregarservicio";
        }

        try {
            ListaFotos listaFotos = new ListaFotos();
            listaFotosRepository.save(listaFotos);
            Fotos foto = new Fotos();
            foto.setFoto(file.getBytes());
            foto.setFotoNombre(fileName);
            foto.setFotoTipoArchivo(file.getContentType());
            foto.setListaFotos(listaFotos);
            fotosRepository.save(foto);
            EspaciosDeportivos espaciosDeportivos = servicioDTO.getEspacio();
            espaciosDeportivos.setListaFotos(listaFotos);
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

//reservas uwu

    @GetMapping("/reservas")
        public String Reservas(Model model){
        return "admin/reservas";
    }


}

