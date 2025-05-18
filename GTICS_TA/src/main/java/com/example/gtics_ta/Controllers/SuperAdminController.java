package com.example.gtics_ta.Controllers;
import com.example.gtics_ta.Entity.Rol;
import com.example.gtics_ta.Repository.RolRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.example.gtics_ta.Repository.UsuarioRepository;
import com.example.gtics_ta.Entity.Usuario;

import javax.naming.Binding;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/SuperAdmin")
public class SuperAdminController {

    private final UsuarioRepository usuarioRepository;

    // Usando inyección por constructor
    @Autowired
    public SuperAdminController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    @Autowired
    private RolRepository rolRepository;

    @GetMapping(value = {"","/"})
    public String Dashboard(Model model) {
        return "Usuario_Superadmin/Dashboard";
    }

    @GetMapping("/usuarios-baneados")
    public String listarUsuariosBaneados(Model model) {
        List<Usuario> baneados = usuarioRepository.findByActivo(false);
        model.addAttribute("baneados", baneados);
        return "Usuario_Superadmin/Baneos"; // Nombre de la vista HTML
    }

    @GetMapping("/usuarios-no-baneados")
    public String listarUsuariosNoBaneados(Model model) {
        // Buscar usuarios cuyo campo isBaneado es falso
        List<Usuario> noBaneados = usuarioRepository.findByActivo(true);
        model.addAttribute("noBaneados", noBaneados);
        return "Usuario_Superadmin/Usuario_main"; // Vista para usuarios no baneados
    }

    @GetMapping("/usuario-formulario")
    public String mostrarFormularioUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        List<Rol> rolesFiltrados = rolRepository.findAll().stream()
                .filter(r -> r.getIdRol() == 3 || r.getIdRol() == 4)
                .collect(Collectors.toList());

        model.addAttribute("roles", rolesFiltrados);

        return "Usuario_Superadmin/Usuario_generar"; // Nombre de tu archivo HTML Thymeleaf
    }

    @PostMapping("/guardar-usuario")
    public String guardarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
                                 BindingResult bindingResult, Model model,
                                 @RequestParam("rolId") Integer rolId) {

        if (bindingResult.hasErrors()) {
            // Volver a enviar SOLO los roles filtrados para que el dropdown tenga solo los correctos
            List<Rol> rolesFiltrados = rolRepository.findAll().stream()
                    .filter(r -> r.getIdRol() == 3 || r.getIdRol() == 4)
                    .collect(Collectors.toList());
            model.addAttribute("roles", rolesFiltrados);
            return "Usuario_Superadmin/Usuario_generar";
        }

        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new IllegalArgumentException("Rol inválido"));
        usuario.setRol(rol);
        usuarioRepository.save(usuario);

        return "redirect:/SuperAdmin/usuarios-no-baneados";
    }


    @GetMapping("/usuarios/banear/{id}")
    public String banearUsuario(@PathVariable("id") Integer id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setActivo(false);  // Marca como baneado
            usuarioRepository.save(usuario);
        }
        return "redirect:/SuperAdmin/usuarios-no-baneados"; // Redirige a usuarios no baneados para que desaparezca de ahí
    }

    @PutMapping("/usuarios/editar/{id}")
    @ResponseBody
    public ResponseEntity<?> editarUsuario(@PathVariable("id") int id, @RequestBody Usuario usuario) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            Usuario userDb = usuarioOptional.get();
            userDb.setNombres(usuario.getNombres());
            userDb.setApellidos(usuario.getApellidos());
            userDb.setNumCelular(usuario.getNumCelular());
            userDb.setDireccion(usuario.getDireccion());

            usuarioRepository.save(userDb);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/usuarios/desbanear/{id}")
    public String desbanearUsuario(@PathVariable("id") Integer id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setActivo(true);  // Quita la marca de baneado
            usuarioRepository.save(usuario);
        }
        return "redirect:/SuperAdmin/usuarios-baneados"; // Recarga la lista de baneados
    }


}


