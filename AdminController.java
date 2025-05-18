package com.example.gtics_ta.Controllers;

import com.example.gtics_ta.DTO.ServicioDTO;
import com.example.gtics_ta.Entity.*;
import com.example.gtics_ta.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private HorariosRepository horariosRepository;
    @Autowired
    private EspaciosDeportivosRepository espaciosDeportivosRepository;
    @Autowired
    private PistasAtletismoRepository pistasAtletismoRepository;
    @Autowired
    private EstadiosRepository estadiosRepository;


    // LISTAR TODOS
    @GetMapping("")
    public String listarServicios(Model model) {
        List<EspaciosDeportivos> espacios = espaciosRepository.findAll();
        model.addAttribute("listaEspacios", espacios);
        return "admin/servicios"; // Debes tener este archivo .html
    }

    // FORMULARIO PARA NUEVO
    @GetMapping("/nuevo")
    public String nuevoServicio(@ModelAttribute("servicioDTO") ServicioDTO servicioDTO, Model model) {
        model.addAttribute("espacio", new EspaciosDeportivos());
        model.addAttribute("tipos", tipoEspacioRepository.findAll());
        return "admin/agregarservicio"; // El form para crear/editar
    }

    @PutMapping("/actualizar/{id}")
    @ResponseBody
    public ResponseEntity<String> actualizarServicio(@PathVariable("id") int id, @RequestBody EspaciosDeportivos espacioActualizado) {
        EspaciosDeportivos existente = espaciosRepository.findById(id).orElse(null);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        existente.setNombre(espacioActualizado.getNombre());
        existente.setUbicacion(espacioActualizado.getUbicacion());
        existente.setCorreoContacto(espacioActualizado.getCorreoContacto());
        existente.setAforo(espacioActualizado.getAforo());
        existente.setHoraAbre(espacioActualizado.getHoraAbre());
        existente.setHoraCierra(espacioActualizado.getHoraCierra());

        if (espacioActualizado.getTipoEspacio() != null && espacioActualizado.getTipoEspacio().getNombre() != null) {
            Optional<TipoEspacio> opttipo = tipoEspacioRepository.findById(espacioActualizado.getTipoEspacio().getId());
            if (opttipo.isPresent()) {
                TipoEspacio tipo = opttipo.get();
                existente.setTipoEspacio(tipo);
            }
        }

        espaciosRepository.save(existente);
        return ResponseEntity.ok("Actualizado correctamente");
    }


    // GUARDAR NUEVO O EDITADO
    @PostMapping("/guardar")
    public String guardarServicio(@ModelAttribute("espacio") EspaciosDeportivos espacio) {
        espaciosRepository.save(espacio);
        return "redirect:/admin/servicios";
    }

    // FORMULARIO PARA EDITAR
    @GetMapping("/editar/{id}")
    public String editarServicio(@PathVariable("id") int id, Model model) {
        EspaciosDeportivos espacio = espaciosRepository.findById(id).orElse(null);
        model.addAttribute("espacio", espacio);
        model.addAttribute("tipos", tipoEspacioRepository.findAll());
        return "admin/agregarservicio";
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


    @GetMapping()

    @PostMapping("/guardarservicio")
    public String guardarServicio(@ModelAttribute("servicioDTO") ServicioDTO servicioDTO){
        EspaciosDeportivos espaciosDeportivos = servicioDTO.getEspacio();
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

        return "redirect:/admin/servicios";
    }


}

