package com.example.gtics_ta.controller;

import com.example.gtics_ta.Entity.EspaciosDeportivos;
import com.example.gtics_ta.Entity.Piscina;
import com.example.gtics_ta.Entity.TipoEspacio;
import com.example.gtics_ta.repositories.EspacioDeportivoDTO;
import com.example.gtics_ta.repositories.EspaciosDeportivosRepository;
import com.example.gtics_ta.repositories.TipoEspacioRepository;
import com.example.gtics_ta.repositories.PiscinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/servicios")
public class EspaciosDeportivosController {

    @Autowired
    EspaciosDeportivosRepository espaciosDeportivosRepository;

    @Autowired
    PiscinaRepository piscinaRepository;

    @Autowired
    private TipoEspacioRepository tipoEspacioRepository;


    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarServicio(@PathVariable("id") Integer id, @RequestBody EspaciosDeportivos datosActualizados) {
        return espaciosDeportivosRepository.findById(id).map(servicio -> {
            // Actualiza los campos que quieres permitir cambiar
            servicio.setNombre(datosActualizados.getNombre());
            servicio.setUbicacion(datosActualizados.getUbicacion());
            servicio.setAforo(datosActualizados.getAforo());
            servicio.setCorreoContacto(datosActualizados.getCorreoContacto());

            TipoEspacio tipoPersistido = tipoEspacioRepository.findByNombre(datosActualizados.getTipoEspacio().getNombre());
            if (tipoPersistido == null) {
                return ResponseEntity.badRequest().body("TipoEspacio no encontrado");
            }
            servicio.setTipoEspacio(tipoPersistido);

            espaciosDeportivosRepository.save(servicio);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // Nuevo: Eliminar servicio via AJAX
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarServicio(@PathVariable("id") Integer id) {
        if (espaciosDeportivosRepository.existsById(id)) {
            piscinaRepository.findById(id).ifPresent(piscinaRepository::delete);
            espaciosDeportivosRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @GetMapping("")
    public String getServicios(Model model) {
        List<Object[]> listaServicios = espaciosDeportivosRepository.findEspaciosConTipoYHorario();
        model.addAttribute("listaServicios", listaServicios); // Pasamos los datos al modelo
        return "main/Admin_Servicios"; // El nombre de la plantilla Thymeleaf
    }


}

