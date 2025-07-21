package com.example.gtics_ta.Controllers;

import com.example.gtics_ta.Entity.*;
import com.example.gtics_ta.Repository.*;
import com.example.gtics_ta.Services.ImageService;
import com.example.gtics_ta.Services.AsistenciaService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/coordinador")
public class CoordinadorController {
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    private TipoEspacioRepository tipoEspacioRepository;
    @Autowired
    private ComentariosRepository comentariosRepository;
    // TipoComentarioRepository ya no se usa - ahora usamos ENUM
    @Autowired
    private EspaciosDeportivosRepository espaciosDeportivosRepository;
    @Autowired
    private ImageService imageService;

    @Autowired
    private AsistenciaService asistenciaService;

    @Autowired
    private HorariosCoordinadorRepository horariosCoordinadorRepository;

    @GetMapping("/perfil")
    public String coordinadorPerfil(@ModelAttribute("usuario") Usuario usuario, HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioSesion == null) {
            return "redirect:/login";
        }

        // Recargar usuario desde la base de datos para obtener datos más recientes
        Usuario usuarioActualizado = usuarioRepository.findById(usuarioSesion.getId()).orElse(usuarioSesion);

        model.addAttribute("usuario", usuarioActualizado);
        return "coordinador/perfil";
    }

    @PostMapping("/guardarperfil")
    public String guardarPerfil(@ModelAttribute("usuario") @Valid Usuario usuario, BindingResult bindingResult,
                                @RequestParam("archivo") MultipartFile file, HttpSession session, Model model) {
        if(bindingResult.hasErrors()) {
            return "coordinador/perfil";
        }

        if(file.isEmpty()) {
            model.addAttribute("msg", "Debe seleccionar una imagen");
            return "coordinador/perfil";
        }

        try {
            // Obtener usuario real de la sesión
            Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
            if (usuarioSesion == null) {
                model.addAttribute("msg", "Sesión expirada");
                return "coordinador/perfil";
            }

            // Usar el nuevo servicio de imágenes con S3
            imageService.uploadUserProfileImage(usuarioSesion, file);

            // Recargar usuario actualizado desde la base de datos
            Usuario usuarioActualizado = usuarioRepository.findById(usuarioSesion.getId()).orElse(usuarioSesion);

            // Actualizar usuario en sesión con los datos más recientes
            session.setAttribute("usuario", usuarioActualizado);

            model.addAttribute("msg", "Imagen de perfil actualizada exitosamente");
            return "redirect:/coordinador/perfil";
        } catch (Exception e) {
            model.addAttribute("msg", "Error al subir la imagen: " + e.getMessage());
            e.printStackTrace(); // Para ver el error en consola
            return "coordinador/perfil";
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

    @GetMapping("/principal")
    public String mostrarPaginaPrincipal(HttpSession  session, Model model) {
        // Obtener lista de tipos de espacio desde la BD
        List<TipoEspacio> tiposEspacio = tipoEspacioRepository.findAllByOrderByNombreAsc();
        model.addAttribute("tiposEspacio", tiposEspacio);

        // Obtener lista de espacios deportivos para el select
        List<EspaciosDeportivos> espaciosDeportivos = espaciosDeportivosRepository.findAll();
        model.addAttribute("espaciosDeportivos", espaciosDeportivos);

        return "coordinador/principal";
    }




    @PostMapping("/actualizar/{id}")
    public String actualizarPerfilUsuario(@PathVariable Integer id,
                                          @RequestParam("celular") String celular,
                                          @RequestParam("foto") MultipartFile foto) {

        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario != null) {
            usuario.setNumCelular(Integer.parseInt(celular));
            try {
                if (!foto.isEmpty()) {
                    usuario.setFoto(foto.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            usuarioRepository.save(usuario);
        }
        return "redirect:/coordinador/perfil/" + id;
    }

    @PostMapping("/guardar-observacion")
    public String guardarObservacion(@RequestParam("espacioId") Integer espacioId,
                                     @RequestParam("tipoComentario") String tipoComentario,
                                     @RequestParam(value = "prioridad", required = false) String prioridad,
                                     @RequestParam("comentarios") String contenido,
                                     @RequestParam(value = "imagenes", required = false) MultipartFile[] imagenes,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {

        try {
            // Obtener el usuario de la sesión
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Sesión expirada. Por favor, inicie sesión nuevamente.");
                return "redirect:/login";
            }

            // Validar que se haya seleccionado un espacio
            if (espacioId == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un espacio deportivo.");
                return "redirect:/coordinador/principal";
            }

            // Validar que el contenido no esté vacío
            if (contenido == null || contenido.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debe ingresar una descripción.");
                return "redirect:/coordinador/principal";
            }

            // Buscar el espacio deportivo seleccionado
            Optional<EspaciosDeportivos> espacioOpt = espaciosDeportivosRepository.findById(espacioId);
            if (espacioOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El espacio deportivo seleccionado no existe.");
                return "redirect:/coordinador/principal";
            }

            EspaciosDeportivos espacio = espacioOpt.get();

            // Determinar el tipo de comentario basado en la selección del radio button
            Comentarios.TipoComentario tipoComentarioEnum;
            if ("reparacion".equals(tipoComentario)) {
                tipoComentarioEnum = Comentarios.TipoComentario.REPARACION;
            } else {
                tipoComentarioEnum = Comentarios.TipoComentario.COMENTARIO;
            }

            // Crear y guardar el comentario
            Comentarios comentario = new Comentarios();
            comentario.setEspacio(espacio);
            comentario.setUsuario(usuario);
            comentario.setTipoComentario(tipoComentarioEnum);
            comentario.setContenido(contenido.trim());

            // Establecer prioridad solo para reparaciones
            if (tipoComentarioEnum == Comentarios.TipoComentario.REPARACION && prioridad != null) {
                try {
                    Comentarios.PrioridadUsuario prioridadEnum = Comentarios.PrioridadUsuario.valueOf(prioridad.toUpperCase());
                    comentario.setPrioridadUsuario(prioridadEnum);
                } catch (IllegalArgumentException e) {
                    // Si la prioridad no es válida, usar MEDIA por defecto
                    comentario.setPrioridadUsuario(Comentarios.PrioridadUsuario.MEDIA);
                }
            }

            // Manejar subida de imágenes si se proporcionaron
            boolean imagenesSubidas = false;
            if (imagenes != null && imagenes.length > 0 && !imagenes[0].isEmpty()) {
                try {
                    ListaFotos listaFotos = imageService.uploadServiceImages(imagenes);
                    comentario.setListaFotos(listaFotos);
                    imagenesSubidas = true;
                } catch (Exception e) {
                    // Log del error pero continuar con el guardado del comentario
                    System.err.println("Error al subir imágenes: " + e.getMessage());
                    redirectAttributes.addFlashAttribute("warning",
                            "La observación se guardó correctamente, pero hubo un error al subir las imágenes: " + e.getMessage());
                }
            }

            comentariosRepository.save(comentario);

            // Mensaje de éxito
            String tipoMensaje = tipoComentarioEnum == Comentarios.TipoComentario.REPARACION ? "reporte de reparación" : "observación";
            String mensaje = "Su " + tipoMensaje + " ha sido registrado exitosamente para el espacio: " + espacio.getNombre();

            if (imagenesSubidas && imagenes != null) {
                mensaje += " con " + imagenes.length + " imagen(es) adjunta(s)";
            }

            redirectAttributes.addFlashAttribute("success", mensaje);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Ocurrió un error al guardar la observación: " + e.getMessage());
        }

        return "redirect:/coordinador/principal";
    }

    @GetMapping("/mis-observaciones")
    public String verMisObservaciones(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }

        // Obtener todas las observaciones del coordinador
        List<Comentarios> misComentarios = comentariosRepository.findByUsuarioOrderByFechaCreacionDesc(usuario);
        model.addAttribute("comentarios", misComentarios);

        return "coordinador/mis-observaciones";
    }

    @GetMapping("/horarios")
    public String verHorarios(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "coordinador/horarios";
    }

    /**
     * Obtiene los horarios del coordinador para el calendario
     */
    @GetMapping("/mis-horarios")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> obtenerMisHorarios(HttpSession session) {
        try {
            Usuario coordinador = (Usuario) session.getAttribute("usuario");
            if (coordinador == null) {
                return ResponseEntity.status(401).build();
            }

            List<HorariosCoordinador> horarios = horariosCoordinadorRepository.findByUsuario(coordinador);

            List<Map<String, Object>> eventos = horarios.stream().map(horario -> {
                try {

                    Map<String, Object> evento = new HashMap<>();
                    evento.put("id", horario.getId());

                    String espacioNombre = horario.getEspacio() != null ? horario.getEspacio().getNombre() : "null";

                    evento.put("title", espacioNombre);

                    // Conversiones seguras
                    LocalDate fechaInicio = horario.getFechaInicio().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate fechaFin = horario.getFechaFin().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalTime horaEntrada = horario.getHoraEntrada().toLocalTime();
                    LocalTime horaSalida = horario.getHoraSalida().toLocalTime();

                    String start = fechaInicio.atTime(horaEntrada).toString();
                    String end = fechaFin.atTime(horaSalida).toString();

                    evento.put("start", start);
                    evento.put("end", end);
                    evento.put("backgroundColor", "#1DBFC1");
                    evento.put("borderColor", "#1DBFC1");
                    evento.put("textColor", "#ffffff");
                    evento.put("allDay", false);

                    evento.put("nombreEspacio", horario.getEspacio().getNombre());
                    evento.put("tipoEspacio", horario.getEspacio().getTipoEspacio() != null
                            ? horario.getEspacio().getTipoEspacio().getNombre() : "Sin tipo");
                    evento.put("ubicacion", horario.getEspacio() != null && horario.getEspacio().getUbicacion() != null
                            ? horario.getEspacio().getUbicacion() : "No especificada");

                    // Generar URL embebible usando coordenadas si están disponibles
                    String embedUrl = "";
                    if (horario.getEspacio().getLatitud() != null && horario.getEspacio().getLongitud() != null) {
                        // Usar coordenadas para crear URL embebible sin API key (usando modo básico)
                        String lat = horario.getEspacio().getLatitud().toString();
                        String lng = horario.getEspacio().getLongitud().toString();
                        embedUrl = "https://maps.google.com/maps?q=" + lat + "," + lng + "&t=&z=15&ie=UTF8&iwloc=&output=embed";
                    } else if (horario.getEspacio().getMapsUrl() != null && !horario.getEspacio().getMapsUrl().isEmpty()) {
                        // Fallback: usar URL original si no hay coordenadas
                        String mapsUrl = horario.getEspacio().getMapsUrl();
                        if (mapsUrl.contains("q=")) {
                            String coords = mapsUrl.substring(mapsUrl.indexOf("q=") + 2);
                            if (coords.contains("&")) {
                                coords = coords.substring(0, coords.indexOf("&"));
                            }
                            embedUrl = "https://maps.google.com/maps?q=" + coords + "&t=&z=15&ie=UTF8&iwloc=&output=embed";
                        }
                    }
                    evento.put("urlMapa", embedUrl);
                    evento.put("horaEntrada", horaEntrada.toString());
                    evento.put("horaSalida", horaSalida.toString());
                    evento.put("fechaInicio", fechaInicio.toString());
                    evento.put("fechaFin", fechaFin.toString());

                    return evento;

                } catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());

            return ResponseEntity.ok(eventos);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // ==================== ENDPOINTS DE ASISTENCIA ====================

    /**
     * Obtiene los espacios deportivos con coordenadas para validación de ubicación
     */
    @GetMapping("/espacios-coordenadas")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerEspaciosCoordenadas() {
        try {
            List<EspaciosDeportivos> espacios = asistenciaService.obtenerEspaciosConCoordenadas();

            Map<String, Object> response = new HashMap<>();
            response.put("espacios", espacios);
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Registra la entrada del coordinador
     */
    @PostMapping("/registrar-entrada")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registrarEntrada(@RequestParam("latitud") BigDecimal latitud,
                                                                @RequestParam("longitud") BigDecimal longitud,
                                                                HttpSession session) {
        Map<String, Object> response = new HashMap<>();


        try {
            Usuario coordinador = (Usuario) session.getAttribute("usuario");
            if (coordinador == null) {
                response.put("success", false);
                response.put("error", "Sesión expirada");
                return ResponseEntity.status(401).body(response);
            }

            Asistencia asistencia = asistenciaService.registrarEntrada(coordinador, latitud, longitud);

            response.put("success", true);
            response.put("message", "Entrada registrada exitosamente");
            response.put("horaEntrada", asistencia.getHoraEntrada().toString());
            response.put("estado", asistencia.getEstadoAsistencia().name());
            if (asistencia.getMinutosRetraso() > 0) {
                response.put("minutosRetraso", asistencia.getMinutosRetraso());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * Registra la salida del coordinador
     */
    @PostMapping("/registrar-salida")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registrarSalida(@RequestParam("latitud") BigDecimal latitud,
                                                               @RequestParam("longitud") BigDecimal longitud,
                                                               HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            Usuario coordinador = (Usuario) session.getAttribute("usuario");
            if (coordinador == null) {
                response.put("success", false);
                response.put("error", "Sesión expirada");
                return ResponseEntity.status(401).body(response);
            }

            Asistencia asistencia = asistenciaService.registrarSalida(coordinador, latitud, longitud);

            response.put("success", true);
            response.put("message", "Salida registrada exitosamente");
            response.put("horaSalida", asistencia.getHoraSalida().toString());
            response.put("horasTrabajadas", asistencia.getHorasTrabajadas());
            response.put("estado", asistencia.getEstadoAsistencia().name());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * Obtiene el estado actual de asistencia del coordinador
     */
    @GetMapping("/estado-asistencia")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerEstadoAsistencia(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            Usuario coordinador = (Usuario) session.getAttribute("usuario");
            if (coordinador == null) {
                response.put("success", false);
                response.put("error", "Sesión expirada");
                return ResponseEntity.status(401).body(response);
            }

            Optional<Asistencia> asistenciaOpt = asistenciaService.obtenerAsistenciaHoy(coordinador);

            if (asistenciaOpt.isPresent()) {
                Asistencia asistencia = asistenciaOpt.get();
                response.put("success", true);
                response.put("tieneEntrada", asistencia.getHoraEntrada() != null);
                response.put("tieneSalida", asistencia.getHoraSalida() != null);

                if (asistencia.getHoraEntrada() != null) {
                    response.put("horaEntrada", asistencia.getHoraEntrada().toString());
                }
                if (asistencia.getHoraSalida() != null) {
                    response.put("horaSalida", asistencia.getHoraSalida().toString());
                    response.put("horasTrabajadas", asistencia.getHorasTrabajadas());
                }
                response.put("estado", asistencia.getEstadoAsistencia().name());
            } else {
                response.put("success", true);
                response.put("tieneEntrada", false);
                response.put("tieneSalida", false);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

}