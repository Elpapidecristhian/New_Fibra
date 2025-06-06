package com.example.gtics_ta.Controllers;

import com.example.gtics_ta.DTO.ReniecDTO;
import com.example.gtics_ta.Entity.AccountActivate;
import com.example.gtics_ta.Entity.Usuario;
import com.example.gtics_ta.Repository.AccountActivateRepository;
import com.example.gtics_ta.Repository.RolRepository;
import com.example.gtics_ta.Repository.UsuarioRepository;
import com.example.gtics_ta.Services.MailService;
import com.example.gtics_ta.Services.ReniecService;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/signup")
public class SignUpController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ReniecService reniecService;

    @Autowired
    private AccountActivateRepository accountActivateRepository;

    @Autowired
    private MailService emailService;

    @GetMapping(value = {"", "/"})
    public String mostrarFormularioRegistro(@ModelAttribute("usuario") Usuario usuario, Model model) {
        model.addAttribute("hoy", LocalDate.now());
        return "/login/signup";
    }

    @PostMapping("/save")
    public String registrarUsuario(@ModelAttribute("usuario") @Valid Usuario usuario,
                                   BindingResult bindingResult,
                                   @RequestParam("confirmarContrasenia") String confirmarContrasenia,
                                   RedirectAttributes attr,
                                   Model model) {
        if(bindingResult.hasErrors()) {
            return "/login/signup";
        }

        if (!usuario.getContrasenia().equals(confirmarContrasenia)) {
            model.addAttribute("errorPswd", "Las contraseñas no coinciden.");
            return "/login/signup";
        }

        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            model.addAttribute("errorEmail", "El correo ya está registrado.");
            return "/login/signup";
        }

        if (usuarioRepository.existsByDni(usuario.getDni())) {
            model.addAttribute("errorDNI", "El DNI ya está registrado.");
            return "/login/signup";
        }


        usuario.setContrasenia(passwordEncoder.encode(usuario.getContrasenia()));
        usuario.setActivo(true);
        usuario.setRol(rolRepository.findByNombre("Vecino"));
        usuario.setActivo(false);

        usuarioRepository.save(usuario);

        String token = UUID.randomUUID().toString();
        AccountActivate accountActivate = new AccountActivate();
        accountActivate.setUsuario(usuario);
        accountActivate.setToken(token);
        accountActivateRepository.save(accountActivate);

        String link = "3.89.234.107:8080/signup/activarcuenta?token=" + token;
        String asunto = "Hola " + usuario.getNombres() + " " + usuario.getApellidos() + ".\n" +
                        "Para activar tu cuenta solo tienes que entrar al siguente enlace: " + link;

        emailService.enviarCorreo(usuario.getCorreo(), "Activa tu cuenta", asunto);

        attr.addFlashAttribute("msg", "Usuario registrado correctamente. \nRevise su correo para activar su cuenta");
        return "redirect:/login";
    }

    @GetMapping("/activarcuenta")
    public String activarCuenta(@RequestParam("token") String token, Model model) {
        AccountActivate activate = accountActivateRepository.findByToken(token);
        if (activate == null) {
            return "/login/login";
        } else {
            Usuario usuario = activate.getUsuario();
            usuario.setActivo(true);
            usuarioRepository.save(usuario);
            accountActivateRepository.delete(activate);
            model.addAttribute("usuario", usuario);
            return "/login/cuentaactiva";
        }
    }

    @PostMapping("/buscardni")
    public String buscarDNI(@RequestParam("dni") String dni, Model model, @ModelAttribute("usuario") Usuario usuario) {
        try {
            ReniecDTO respuesta = reniecService.consultaPorDNI(dni);
            if (respuesta != null) {
                usuario.setNombres(respuesta.getNombres());
                usuario.setApellidos(respuesta.getApellidoPaterno() + " " + respuesta.getApellidoMaterno());
                usuario.setDni(Integer.valueOf(dni));
            }
            model.addAttribute("usuario", usuario);
            model.addAttribute("hoy", LocalDate.now());
            return "/login/signup";
        } catch (Exception e) {
            model.addAttribute("errorDNIConsulta", "No se pudo encontrar información del DNI");
            model.addAttribute("hoy", LocalDate.now());
            return "/login/signup";
        }
    }


}
