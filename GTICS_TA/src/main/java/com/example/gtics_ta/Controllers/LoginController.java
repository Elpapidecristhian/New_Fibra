package com.example.gtics_ta.Controllers;

import com.example.gtics_ta.Entity.PasswordReset;
import com.example.gtics_ta.Entity.Usuario;
import com.example.gtics_ta.Repository.PasswordResetRepository;
import com.example.gtics_ta.Repository.UsuarioRepository;
import com.example.gtics_ta.Services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private PasswordResetRepository passwordResetRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private MailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping(value = {"","/"})
    public String mostrarLogin(){
        return"login/login";
    }

    @GetMapping("/recoverpassword")
    public String mostrarFormularioRecover() {return "login/recoverpass";}

    @PostMapping("/requestrecover")
    public String sendEecoverEmail(@RequestParam String email, Model model, RedirectAttributes attr) {
        if(email!= null){
            if(usuarioRepository.existsByCorreo(email)) {
                String token = UUID.randomUUID().toString();
                String link = "https://sanmigueldeportes.online/login/resetpassword?token=" + token;

                Usuario usuario = usuarioRepository.findByCorreo(email);
                Map<String, Object> datos = new HashMap<>();
                datos.put("nombre", usuario.getNombres() + " " + usuario.getApellidos());
                datos.put("url", link);

                emailService.enviarCorreoConPlantilla(email, "Recupera tu contraseña", "email/recuperarPasswd", datos);

                PasswordReset passwordReset = new PasswordReset();
                passwordReset.setToken(token);
                passwordReset.setExpiracion(LocalDateTime.now().plusHours(1));
                passwordReset.setUsuario(usuarioRepository.findByCorreo(email));
                passwordResetRepository.save(passwordReset);
                attr.addFlashAttribute("msg", "Un correo con instrucciones para restablecer su contraseña a sido enviado a la dirección de correo registrada");
                return "redirect:/login";
            }else{
                model.addAttribute("msg", "El correo indicado no está registrado");
            }
        }
        model.addAttribute("msg", "El correo indicado no está registrado");
        return "login/recoverpass";
    }

    @GetMapping("/resetpassword")
    public String mostrarFormularioReset(@RequestParam String token, Model model) {
        PasswordReset passwdreset = passwordResetRepository.findByToken(token);
        if (passwdreset == null || passwdreset.getExpiracion().isBefore(LocalDateTime.now())) {
            return "login/login";
        }
        model.addAttribute("token", token);
        return "login/resetpassword";
    }

    @PostMapping("/savenewpassword")
    public String procesarReset(@RequestParam String token, @RequestParam String nuevaContrasenia, @RequestParam String confirmContrasenia, Model model, RedirectAttributes attr) {
        PasswordReset passwordReset = passwordResetRepository.findByToken(token);
        if (passwordReset == null || passwordReset.getExpiracion().isBefore(LocalDateTime.now())) {
            return "login/login";
        } else {
            if(nuevaContrasenia.length() < 8 ){
                model.addAttribute("errorLength", "La contraseña debe contener al menos 8 caracteres");
                return "login/resetpassword";
            } else {
                if(!nuevaContrasenia.equals(confirmContrasenia)){
                    model.addAttribute("error", "Las contraseñas no coinciden");
                    return "/login/resetpassword";
                }else {
                    Usuario usuario = passwordReset.getUsuario();
                    usuario.setContrasenia(passwordEncoder.encode(nuevaContrasenia));
                    usuarioRepository.save(usuario);
                    passwordResetRepository.delete(passwordReset);
                    attr.addFlashAttribute("msg", "Contraseña restablecida con éxito");
                    return "redirect:/login";
                }
            }
        }
    }





}