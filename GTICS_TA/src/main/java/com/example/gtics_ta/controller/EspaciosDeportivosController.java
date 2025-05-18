package com.example.gtics_ta.controller;

import com.example.gtics_ta.Entity.*;
import com.example.gtics_ta.repositories.*;
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

@Controller
@RequestMapping("/servicios")
public class EspaciosDeportivosController {

    @Autowired
    private EspaciosDeportivosRepository espaciosRepo;

    @Autowired
    private TipoEspacioRepository tipoEspacioRepo;

    @Autowired
    private PiscinaRepository piscinaRepository;

    @Autowired
    private LosaRepository losaRepository;

    @Autowired
    private ListaFotosRepository listaFotosRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private HorariosRepository horariosRepository;


    // LISTAR TODOS
    @GetMapping("")
    public String listarServicios(Model model) {
        List<EspaciosDeportivos> espacios = espaciosRepo.findAll();
        model.addAttribute("listaEspacios", espacios);
        return "main/Admin_Servicios"; // Debes tener este archivo .html
    }

    // FORMULARIO PARA NUEVO
    @GetMapping("/nuevo")
    public String nuevoServicio(Model model) {
        model.addAttribute("espacio", new EspaciosDeportivos());
        model.addAttribute("tipos", tipoEspacioRepo.findAll());
        return "main/Admin_AgregarServicio"; // El form para crear/editar
    }

    @PutMapping("/actualizar/{id}")
    @ResponseBody
    public ResponseEntity<String> actualizarServicio(@PathVariable("id") int id, @RequestBody EspaciosDeportivos espacioActualizado) {
        EspaciosDeportivos existente = espaciosRepo.findById(id).orElse(null);
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
            TipoEspacio tipo = tipoEspacioRepo.findByNombre(espacioActualizado.getTipoEspacio().getNombre());
            if (tipo != null) {
                existente.setTipoEspacio(tipo);
            }
        }

        espaciosRepo.save(existente);
        return ResponseEntity.ok("Actualizado correctamente");
    }


    // GUARDAR NUEVO O EDITADO
    @PostMapping("/guardar")
    public String guardarServicio(@ModelAttribute("espacio") EspaciosDeportivos espacio) {
        espaciosRepo.save(espacio);
        return "redirect:/admin/servicios";
    }

    // FORMULARIO PARA EDITAR
    @GetMapping("/editar/{id}")
    public String editarServicio(@PathVariable("id") int id, Model model) {
        EspaciosDeportivos espacio = espaciosRepo.findById(id).orElse(null);
        model.addAttribute("espacio", espacio);
        model.addAttribute("tipos", tipoEspacioRepo.findAll());
        return "main/Admin_AgregarServicio";
    }

    @DeleteMapping("/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminarServicio(@PathVariable("id") int id) {
        if (!espaciosRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        espaciosRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/subir-foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> subirFoto(@RequestParam("file") MultipartFile file) {
        try {
            String urlPublica = "https://miservidor.com/uploads/" + file.getOriginalFilename();

            ListaFotos listaFoto = new ListaFotos();
            listaFoto.setNombreArchivo(file.getOriginalFilename());
            listaFoto.setUrl(urlPublica);

            listaFotosRepository.save(listaFoto);

            return ResponseEntity.ok(Collections.singletonMap("id", listaFoto.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }





    @PostMapping("/registrar/losa")
    @ResponseBody
    public ResponseEntity<String> registrarLosa(@RequestBody Map<String, Object> data) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            // Campos básicos del espacio
            String nombre = (String) data.get("nombre");
            String ubicacion = (String) data.get("ubicacion");
            String descripcion = (String) data.get("descripcion");
            String tipoEspacioStr = (String) data.get("tipoEspacio");
            String correoContacto = (String) data.get("correoContacto"); // NUEVO CAMPO

            // Horarios
            String horaAbreStr = (String) data.get("horaAbre");
            String horaCierraStr = (String) data.get("horaCierra");

            LocalTime horaAbre = LocalTime.parse(horaAbreStr, formatter);
            LocalTime horaCierra = LocalTime.parse(horaCierraStr, formatter);

            // Aforo
            Integer aforo = null;
            Object aforoObj = data.get("aforo");
            if (aforoObj != null) {
                try {
                    if (aforoObj instanceof Number) {
                        aforo = ((Number) aforoObj).intValue();
                    } else if (aforoObj instanceof String && !((String) aforoObj).isBlank()) {
                        aforo = Integer.parseInt((String) aforoObj);
                    }
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body("Aforo no válido: " + aforoObj);
                }
            }

            // Datos específicos para losa
            String tipoDeporte = (String) data.get("tipoDeporte");
            String tamanoCampo = (String) data.get("tamanoCampo");
            String iluminacion = (String) data.get("iluminacion");
            Object costoHoraObj = data.get("costoHora");

            if (costoHoraObj == null) {
                return ResponseEntity.badRequest().body("Costo por hora no puede ser nulo");
            }

            Float costoHora;
            try {
                if (costoHoraObj instanceof Number) {
                    costoHora = ((Number) costoHoraObj).floatValue();
                } else {
                    costoHora = Float.parseFloat(costoHoraObj.toString());
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Costo por hora no válido");
            }

            String prohibido = (String) data.get("prohibido");

            // Buscar tipo de espacio
            TipoEspacio tipoEspacio = tipoEspacioRepo.findByNombre(tipoEspacioStr);
            if (tipoEspacio == null) {
                return ResponseEntity.badRequest().body("Tipo de espacio no válido");
            }

            // Buscar ListaFotos por ID enviado en JSON
            Object idListaFotosObj = data.get("idListaFotos");
            if (idListaFotosObj == null) {
                return ResponseEntity.badRequest().body("ID de ListaFotos es requerido");
            }

            Integer idListaFotos;
            try {
                if (idListaFotosObj instanceof Number) {
                    idListaFotos = ((Number) idListaFotosObj).intValue();
                } else {
                    idListaFotos = Integer.parseInt(idListaFotosObj.toString());
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("ID de ListaFotos no válido");
            }

            ListaFotos listaFotos = listaFotosRepository.findById(idListaFotos).orElse(null);
            if (listaFotos == null) {
                return ResponseEntity.badRequest().body("ListaFotos no encontrada");
            }

            // Crear objeto principal
            EspaciosDeportivos espacio = new EspaciosDeportivos();
            espacio.setNombre(nombre);
            espacio.setUbicacion(ubicacion);
            espacio.setDescripcionCorta(descripcion);
            espacio.setTipoEspacio(tipoEspacio);
            espacio.setCostoHorario(costoHora);
            espacio.setHoraAbre(horaAbre);
            espacio.setHoraCierra(horaCierra);
            espacio.setAforo(aforo);
            espacio.setCorreoContacto(correoContacto); // NUEVO SETTER
            espacio.setOperativo(true); // default para que no falle si es NOT NULL
            espacio.setNumContacto(0); // puedes cambiarlo por un valor real si lo necesitas
            espacio.setListaFotos(listaFotos);
            espaciosRepo.save(espacio);

            // Crear losa
            Losa losa = new Losa();
            losa.setEspacio(espacio);
            losa.setTipoDeporte(tipoDeporte);
            losa.setTamanoCampo(tamanoCampo);
            losa.setIluminacion(iluminacion);
            losa.setCostoHora(costoHora);
            losa.setProhibido(prohibido);

            losaRepository.save(losa);

            return ResponseEntity.ok("Losa registrada");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al registrar losa: " + e.getMessage());
        }
    }


    @PostMapping("/registrar/piscina")
    @ResponseBody
    public ResponseEntity<String> registrarPiscina(@RequestBody Piscina piscina) {
        try {
            // Validar que el espacio deportivo venga en el JSON
            EspaciosDeportivos espacio = piscina.getEspacio();
            if (espacio == null) {
                return ResponseEntity.badRequest().body("Espacio deportivo es requerido");
            }

            // Guardar primero el espacio deportivo para que se genere el idEspacio
            espaciosRepo.save(espacio);

            // Asignar el idEspacio generado a la piscina
            piscina.setIdEspacio(espacio.getId());

            // Guardar la piscina
            piscinaRepository.save(piscina);

            return ResponseEntity.ok("Piscina registrada correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al registrar piscina: " + e.getMessage());
        }
    }





}

