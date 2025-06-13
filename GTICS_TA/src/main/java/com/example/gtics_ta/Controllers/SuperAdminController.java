package com.example.gtics_ta.Controllers;
import com.example.gtics_ta.DTO.ResumenDTO;
import com.example.gtics_ta.DTO.SuperadminDTO;
import com.example.gtics_ta.Entity.Rol;
import com.example.gtics_ta.Repository.EspaciosDeportivosRepository;
import com.example.gtics_ta.Repository.ReservasRepository;
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


import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    @Autowired
    private EspaciosDeportivosRepository espaciosDeportivosRepository;

    @Autowired
    private ReservasRepository reservaRepository;

    @GetMapping(value = {"","/"})
    public String Dashboard(Model model) {
        SuperadminDTO dto = new SuperadminDTO();

        dto.setTotalUsuarios(usuarioRepository.count());
        dto.setTotalUsuariosBaneados(usuarioRepository.countByActivo(false));
        dto.setEspaciosDisponibles(espaciosDeportivosRepository.countByOperativo(true));
        dto.setEspaciosOcupados(espaciosDeportivosRepository.countByOperativo(false));
        List<Object[]> porcentajes = reservaRepository.porcentajeReservasPorServicio();
        List<String> nombres = new ArrayList<>();
        List<Long> cantidades = new ArrayList<>();

        for (Object[] fila : porcentajes) {
            nombres.add((String) fila[0]);
            cantidades.add(((Number) fila[1]).longValue());
        }

        dto.setNombresServiciosPorcentaje(nombres);
        dto.setCantidadServiciosPorcentaje(cantidades);


        List<Object[]> topServicios = reservaRepository.top10ServiciosMasReservados();
        List<String> nombresServicios = new ArrayList<>();
        List<Long> cantidadReservas = new ArrayList<>();

        for (Object[] fila : topServicios) {
            nombresServicios.add((String) fila[0]);
            cantidadReservas.add(((Number) fila[1]).longValue());
        }

        dto.setNombresServiciosTop(nombresServicios);
        dto.setCantidadReservasTop(cantidadReservas);

        List<Object[]> resultados = reservaRepository.resumenUltimosTresMesesRaw();
        Object[] fila = resultados.isEmpty() ? new Object[]{0.0, 0L} : resultados.get(0);

        ResumenDTO resumen3m = new ResumenDTO(
                fila[0] != null ? ((Number) fila[0]).doubleValue() : 0.0,
                fila[1] != null ? ((Number) fila[1]).longValue() : 0L
        );

        dto.setTotalRecaudadoUltimos3Meses(resumen3m.getTotal());
        dto.setReservasUltimos3Meses(resumen3m.getCantidad().intValue());

        ResumenDTO resumenAnual = reservaRepository.resumenAnual();
        dto.setTotalRecaudadoAnual(resumenAnual.getTotal());
        dto.setReservasAnuales((int) resumenAnual.getCantidad().longValue());

        model.addAttribute("dashboard", dto);
        List<Object[]> dataMensual = reservaRepository.reporteMensualUltimos3Meses();
        List<String> meses = new ArrayList<>();
        List<Double> totales = new ArrayList<>();
        List<Long> cantidades3Meses  = new ArrayList<>();
        List<Object[]> dataAnual = reservaRepository.reporteMensualAnual(); // <-- asegúrate de tener esta query
        List<String> mesesAnual = new ArrayList<>();
        List<Double> totalesAnual = new ArrayList<>();
        List<Long> cantidadesAnual = new ArrayList<>();

        for (Object[] filaMensual : dataMensual) {
            int numeroMes = ((Number) filaMensual[0]).intValue();  // 1=enero, 2=febrero, ...
            Month mesEnum = Month.of(numeroMes);
            String mesEnEspanol = mesEnum.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
            mesEnEspanol = Character.toUpperCase(mesEnEspanol.charAt(0)) + mesEnEspanol.substring(1);
            meses.add(mesEnEspanol);
            totales.add(((Number) filaMensual[1]).doubleValue());
            cantidades3Meses.add(((Number) filaMensual[2]).longValue());
        }
        for (Object[] filaAnual : dataAnual) {
            int numeroMes = ((Number) filaAnual[0]).intValue();  // 1=enero, 2=febrero, ...
            Month mesEnum = Month.of(numeroMes);
            String mesEnEspanol = mesEnum.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
            mesEnEspanol = Character.toUpperCase(mesEnEspanol.charAt(0)) + mesEnEspanol.substring(1);
            mesesAnual.add(mesEnEspanol);
            totalesAnual.add(((Number) filaAnual[1]).doubleValue());
            cantidadesAnual.add(((Number) filaAnual[2]).longValue());
        }

        dto.setMesesAnuales(mesesAnual);
        dto.setRecaudacionAnualPorMes(totalesAnual);
        dto.setReservasAnualesPorMes(cantidadesAnual);
        dto.setMesesUltimos3Meses(meses);
        dto.setRecaudacionUltimos3Meses(totales);
        dto.setReservasUltimos3MesesLista(cantidades3Meses);
        dto.setCantidadTotalReservas(reservaRepository.contarTotalReservas());
        dto.setCantidadReservasHoy(reservaRepository.contarReservasHoy());

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