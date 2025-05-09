package com.example.gtics_ta.controller;
import com.example.gtics_ta.entity.Fotos;  // entidad Foto
import com.example.gtics_ta.entity.EspaciosDeportivos;
import com.example.gtics_ta.repository.EspaciosDeportivosRepository;
import com.example.gtics_ta.repository.FotosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;


@Controller
public class EspaciosDeportivosController {

    private final EspaciosDeportivosRepository repository;
    @Autowired
    private FotosRepository fotoRepository;
    public EspaciosDeportivosController(EspaciosDeportivosRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/espacios")
    public String listarEspacios(
            @RequestParam(name = "tipo", required = false) String tipo,
            @RequestParam(name = "fecha", required = false) String fecha,
            Model model
    ) {
        List<EspaciosDeportivos> espacios;

        if (tipo != null && !tipo.isEmpty()) {
            espacios = repository.findByTipoEspacio_NombreTipo(tipo);
        } else {
            espacios = repository.findAll();
        }

        model.addAttribute("espacios", espacios);
        model.addAttribute("tipoSeleccionado", tipo);
        model.addAttribute("fechaSeleccionada", fecha);

        return "vecino/vecino_espacios_deportivos";  // nombre del HTML
    }

    @GetMapping("/imagen/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable("id") Integer id) {
        Optional<Fotos> optionalFoto = fotoRepository.findById(id);
        if (optionalFoto.isPresent()) {
            Fotos foto = optionalFoto.get();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                    .body(foto.getFoto()); // Asumiendo que es byte[]
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
