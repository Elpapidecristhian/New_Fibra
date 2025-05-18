package com.example.gtics_ta.controller;

import com.example.gtics_ta.Entity.*;
import com.example.gtics_ta.dto.HorarioReservadoDTO;
import com.example.gtics_ta.dto.UsuarioDTO;
import com.example.gtics_ta.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private HorarioReservadoRepository horarioReservadoRepo;

    @Autowired
    private HorariosRepository horariosRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("")
    public String listarServicios(Model model) {
        List<EspaciosDeportivos> espacios = espaciosRepo.findAll();
        model.addAttribute("listaEspacios", espacios);
        return "Administrador/Admin_Servicios"; // Debes tener este archivo .html
    }

    @GetMapping("/nuevo")
    public String nuevoServicio(Model model) {
        model.addAttribute("espacio", new EspaciosDeportivos());
        model.addAttribute("tipos", tipoEspacioRepo.findAll());
        return "Administrador/Admin_AgregarServicio"; // El form para crear/editar
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


    @PostMapping("/guardar")
    public String guardarServicio(@ModelAttribute("espacio") EspaciosDeportivos espacio) {
        espaciosRepo.save(espacio);
        return "redirect:/Administrador/servicios";
    }

    @GetMapping("/editar/{id}")
    public String editarServicio(@PathVariable("id") int id, Model model) {
        EspaciosDeportivos espacio = espaciosRepo.findById(id).orElse(null);
        model.addAttribute("espacio", espacio);
        model.addAttribute("tipos", tipoEspacioRepo.findAll());
        return "Administrador/Admin_AgregarServicio";
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

            String nombre = (String) data.get("nombre");
            String ubicacion = (String) data.get("ubicacion");
            String descripcion = (String) data.get("descripcion");
            String tipoEspacioStr = (String) data.get("tipoEspacio");
            String correoContacto = (String) data.get("correoContacto"); // NUEVO CAMPO

            String horaAbreStr = (String) data.get("horaAbre");
            String horaCierraStr = (String) data.get("horaCierra");

            LocalTime horaAbre = LocalTime.parse(horaAbreStr, formatter);
            LocalTime horaCierra = LocalTime.parse(horaCierraStr, formatter);

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

            TipoEspacio tipoEspacio = tipoEspacioRepo.findByNombre(tipoEspacioStr);
            if (tipoEspacio == null) {
                return ResponseEntity.badRequest().body("Tipo de espacio no válido");
            }

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

            EspaciosDeportivos espacio = new EspaciosDeportivos();
            espacio.setNombre(nombre);
            espacio.setUbicacion(ubicacion);
            espacio.setDescripcionCorta(descripcion);
            espacio.setTipoEspacio(tipoEspacio);
            espacio.setCostoHorario(costoHora);
            espacio.setHoraAbre(horaAbre);
            espacio.setHoraCierra(horaCierra);
            espacio.setAforo(aforo);
            espacio.setCorreoContacto(correoContacto);
            espacio.setOperativo(true);
            espacio.setNumContacto(0);
            espacio.setListaFotos(listaFotos);
            espaciosRepo.save(espacio);

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

// -----------------------------------------------------------------------------------

    @GetMapping("/reservas")
    public String listarReservas(Model model) {
        List<Reservas> reservas = reservaRepository.findAll();
        List<HorarioReservado> mantenimientos = horarioReservadoRepo.findByEstado("Mantenimiento");

        model.addAttribute("listaReservas", reservas);
        model.addAttribute("listaMantenimientos", mantenimientos);
        return "Administrador/Admin_Reservas";
    }



    @GetMapping("/horariosReservados")
    @ResponseBody
    public List<HorarioReservado> getHorariosReservados(
            @RequestParam Integer idEspacio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<String> estados = List.of("Reservado", "Mantenimiento");

        return horarioReservadoRepo.findByEspacioIdAndFechaAndEstados(idEspacio, fecha, estados);
    }

    @PostMapping("/horarioReservado")
    @ResponseBody
    public ResponseEntity<String> crearHorarioReservado(@RequestBody HorarioReservadoDTO dto) {
        try {
            LocalDate fecha = LocalDate.parse(dto.getFecha());
            LocalTime horaInicio = LocalTime.parse(dto.getHoraInicio());
            LocalTime horaFin = LocalTime.parse(dto.getHoraFin());

            Optional<EspaciosDeportivos> optEspacio = espaciosRepo.findById(dto.getIdEspacio());
            if (optEspacio.isEmpty()) return ResponseEntity.badRequest().body("Espacio no encontrado");
            EspaciosDeportivos espacio = optEspacio.get();

            Optional<Horarios> horarioOpt = horariosRepository.findByHoraInicioAndHoraFinAndEspacio(horaInicio, horaFin, espacio);
            if (horarioOpt.isEmpty()) return ResponseEntity.badRequest().body("Horario inválido para este espacio");

            HorarioReservado horarioReservado = new HorarioReservado();
            horarioReservado.setFecha(fecha);
            horarioReservado.setHorario(horarioOpt.get());
            horarioReservado.setEstado(dto.getEstado()); // "Reservado" o "Mantenimiento"

            horarioReservadoRepo.save(horarioReservado);

            return ResponseEntity.ok("Horario reservado o en mantenimiento registrado");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/usuario/{id}")
    @ResponseBody
    public ResponseEntity<UsuarioDTO> obtenerDatosUsuario(@PathVariable("id") Integer idUsuario) {
        UsuarioDTO usuarioDTO = usuarioRepository.findUsuarioDTOById(idUsuario);
        if (usuarioDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuarioDTO);
    }

    @DeleteMapping("/horarioReservado/{id}")
    @ResponseBody
    public ResponseEntity<String> eliminarMantenimiento(@PathVariable("id") Integer id) {
        Optional<HorarioReservado> opt = horarioReservadoRepo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HorarioReservado horario = opt.get();
        if (!"Mantenimiento".equals(horario.getEstado())) {
            return ResponseEntity.badRequest().body("Solo se puede eliminar mantenimientos.");
        }

        horarioReservadoRepo.delete(horario);
        return ResponseEntity.ok("Mantenimiento eliminado correctamente");
    }



}

