package com.example.gtics_ta.Controllers;

import com.example.gtics_ta.Entity.*;
import com.example.gtics_ta.Repository.*;
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
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/coordinador")
public class CoordinadorController {
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    private TipoEspacioRepository tipoEspacioRepository;
    @Autowired
    private ComentariosRepository comentariosRepository;
    @Autowired
    private TipoComentarioRepository tipoComentarioRepository;
    @Autowired
    private EspaciosDeportivosRepository espaciosDeportivosRepository;

    @GetMapping("/perfil")
    public String coordinadorPerfil(@ModelAttribute("usuario") Usuario usuario, HttpSession session, Model model) {
        usuario = (Usuario) session.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        return "coordinador/perfil";
    }

    @PostMapping("/guardarperfil")
    public String guardarPerfil(@ModelAttribute("usuario") @Valid Usuario usuario, BindingResult bindingResult, @RequestParam("archivo") MultipartFile file , Model model) {
        if(bindingResult.hasErrors()) {
            return "coordinador/perfil";
        }

        if(file.isEmpty()) {
            return "coordinador/perfil";
        }

        String fileName = file.getOriginalFilename();

        if (fileName.contains("..")){
            model.addAttribute("msg","Debe ingresar un archivo válido");
            return "coordinador/perfil";
        }

        try {
            usuario.setFoto(file.getBytes());
            usuario.setFotoNombre(fileName);
            usuario.setFotoTipoArchivo(file.getContentType());
            usuarioRepository.save(usuario);
            return "redirect:/coordinador/perfil?id=" + usuario.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return "coordinador/perfil";
        }
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> mostrarImagen(@PathVariable("id") Integer id) {
        Optional<Usuario> optusuario = usuarioRepository.findById(id);
        if(optusuario.isPresent()) {
            Usuario usuario = optusuario.get();

            byte[] image = usuario.getFoto();
            if (image == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            String tipoArchivo = usuario.getFotoTipoArchivo();
            if (tipoArchivo == null || tipoArchivo.isBlank()) {
                tipoArchivo = "application/octet-stream"; // tipo MIME por defecto
            }

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(
                    MediaType.parseMediaType(usuario.getFotoTipoArchivo()));

            return new ResponseEntity<>(
                    image,
                    httpHeaders,
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
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

    @GetMapping("/foto/{id}")
    public ResponseEntity<byte[]> mostrarFoto(@PathVariable Integer id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario != null && usuario.getFoto() != null) {
            byte[] foto = usuario.getFoto();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // O ajusta según tu formato
            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
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
    public String guardarObservacion(@RequestParam("tipoServicio") Integer tipoServicioId,
                                     @RequestParam("tipoComentario") String tipoComentario,
                                     @RequestParam("comentarios") String contenido,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {

        try {
            // Obtener el usuario de la sesión
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Sesión expirada. Por favor, inicie sesión nuevamente.");
                return "redirect:/login";
            }

            // Validar que se haya seleccionado un tipo de servicio
            if (tipoServicioId == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un tipo de servicio.");
                return "redirect:/coordinador/principal";
            }

            // Validar que el contenido no esté vacío
            if (contenido == null || contenido.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debe ingresar una descripción.");
                return "redirect:/coordinador/principal";
            }

            // Buscar el primer espacio deportivo del tipo seleccionado
            List<EspaciosDeportivos> espacios = espaciosDeportivosRepository.findByTipoEspacio_Id(tipoServicioId);
            if (espacios.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No se encontraron espacios para el tipo de servicio seleccionado.");
                return "redirect:/coordinador/principal";
            }

            EspaciosDeportivos espacio = espacios.get(0); // Tomar el primer espacio del tipo

            // BD: 1 = reparacion, 2 = comentario
            Integer tipoComentarioId;
            if ("reparacion".equals(tipoComentario)) {
                tipoComentarioId = 1; // Reparación (según tu BD)
            } else {
                tipoComentarioId = 2; // Comentario general (según tu BD)
            }

            // Buscar el tipo de comentario en la BD
            Optional<TipoComentario> tipoComentarioOpt = tipoComentarioRepository.findById(tipoComentarioId);
            if (tipoComentarioOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Tipo de comentario no válido.");
                return "redirect:/coordinador/principal";
            }

            // Crear y guardar el comentario
            Comentarios comentario = new Comentarios();
            comentario.setEspacio(espacio);
            comentario.setUsuario(usuario);
            comentario.setTipoComentario(tipoComentarioOpt.get());
            comentario.setContenido(contenido.trim());

            comentariosRepository.save(comentario);

            // Mensaje de éxito
            String tipoMensaje = tipoComentarioId == 1 ? "reporte de reparación" : "observación";
            redirectAttributes.addFlashAttribute("success",
                    "Su " + tipoMensaje + " ha sido registrado exitosamente para el espacio: " + espacio.getNombre());

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

}