package com.example.gtics_ta.Controllers;
import com.example.gtics_ta.DTO.AdminDTO;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.properties.*;
import com.example.gtics_ta.DTO.ServicioDTO;
import com.example.gtics_ta.Entity.*;
import com.example.gtics_ta.Repository.*;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EspaciosDeportivosRepository espaciosRepository;

    @Autowired
    private TipoEspacioRepository tipoEspacioRepository;

    @Autowired
    private PiscinasRepository piscinaRepository;

    @Autowired
    private CanchasFutbolRepository canchasFutbolRepository;

    @Autowired
    private ListaFotosRepository listaFotosRepository;

    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private HorariosRepository horariosRepository;
    @Autowired
    private EspaciosDeportivosRepository espaciosDeportivosRepository;
    @Autowired
    private PistasAtletismoRepository pistasAtletismoRepository;
    @Autowired
    private EstadiosRepository estadiosRepository;
    @Autowired
    private FotosRepository fotosRepository;
    @Autowired
    private HorarioReservadoRepository horarioReservadoRepository;


    // LISTAR TODOS
    @GetMapping(value = "/servicios")
    public String listarServicios(Model model) {
        List<EspaciosDeportivos> espacios = espaciosRepository.findAll();
        model.addAttribute("listaEspacios", espacios);
        return "admin/servicios"; // Debes tener este archivo .html
    }

    @GetMapping(value={"", "/"})
    public String Dashboard(Model model) {
        AdminDTO dto = new AdminDTO();

        dto.setTotalUsuarios(usuarioRepository.count());
        dto.setTotalUsuariosBaneados(usuarioRepository.countByActivo(false));
        dto.setEspaciosDisponibles(espaciosDeportivosRepository.countByOperativo(true));
        dto.setCantidadTotalReservas(reservaRepository.contarTotalReservas());
        dto.setCantidadReservasHoy(reservaRepository.contarReservasHoy());

        List<Object[]> topServicios = reservaRepository.top10ServiciosMasReservados();
        List<String> nombresTop = new ArrayList<>();
        List<Long> cantidadesTop = new ArrayList<>();

        for (Object[] fila : topServicios) {
            nombresTop.add((String) fila[0]);
            cantidadesTop.add(((Number) fila[1]).longValue());
        }

        dto.setNombresServiciosTop(nombresTop);
        dto.setCantidadReservasTop(cantidadesTop);

        List<Object[]> porcentajes = reservaRepository.porcentajeReservasPorServicio();
        List<String> nombres = new ArrayList<>();
        List<Long> cantidades = new ArrayList<>();

        for (Object[] fila : porcentajes) {
            nombres.add((String) fila[0]);
            cantidades.add(((Number) fila[1]).longValue());
        }
// TOP 10 USUARIOS
        List<Object[]> topUsuarios = reservaRepository.top10UsuariosConMasReservas();
        List<String> nombresUsuarios = new ArrayList<>();
        List<Long> cantidadUsuarios = new ArrayList<>();

        int count = 0;
        for (Object[] fila : topUsuarios) {
            if (count++ >= 10) break;

            // Asegúrate de castear correctamente cada campo
            String nombrePersona = (String) fila[1];
            String apellidoPersona = (String) fila[2];
            String nombreCompleto = nombrePersona + " " + apellidoPersona;


            nombresUsuarios.add(nombreCompleto);
            cantidadUsuarios.add(((Number) fila[3]).longValue());
        }
        dto.setNombresUsuariosTop(nombresUsuarios);
        dto.setCantidadReservasUsuariosTop(cantidadUsuarios);


// RESERVAS POR HORA
        List<Object[]> porHora = reservaRepository.distribucionReservasPorHora();
        List<String> horas = new ArrayList<>();
        List<Long> cantidadHoras = new ArrayList<>();
        for (Object[] fila : porHora) {
            Integer horaInt = (Integer) fila[0];
            horas.add(String.format("%02d:00", horaInt));  // ej: "08:00"
            cantidadHoras.add(((Number) fila[1]).longValue());
        }
        dto.setHorasReservas(horas);
        dto.setCantidadReservasPorHora(cantidadHoras);

        dto.setNombresServiciosPorcentaje(nombres);
        dto.setCantidadServiciosPorcentaje(cantidades);

        model.addAttribute("dashboard", dto);

        return "admin/dashboard"; // Vista correspondiente
    }

    // FORMULARIO PARA NUEVO
    @GetMapping("/nuevo")
    public String nuevoServicio(@ModelAttribute("servicioDTO") ServicioDTO servicioDTO, Model model) {
        Estadios estadios = new Estadios();
        estadios.setUsoPermitido("-");
        PistasAtletismo pistasAtletismo = new PistasAtletismo();
        pistasAtletismo.setImplementos("-");
        Piscinas piscinas = new Piscinas();
        piscinas.setRequisitos("-");
        servicioDTO.setEstadios(estadios);
        servicioDTO.setPista(pistasAtletismo);
        servicioDTO.setPiscina(piscinas);
        model.addAttribute("tipos", tipoEspacioRepository.findAll());
        return "admin/agregarservicio"; // El form para crear/editar
    }

    @PutMapping("/actualizar/{id}")
    @ResponseBody
    public ResponseEntity<String> actualizarServicio(@PathVariable("id") Integer id, @RequestBody EspaciosDeportivos espacioActualizado) {
        EspaciosDeportivos espacio = espaciosRepository.findById(id).orElse(null);
        if (espacio == null) {
            return ResponseEntity.notFound().build();
        }

        espacio.setNombre(espacioActualizado.getNombre());
        espacio.setUbicacion(espacioActualizado.getUbicacion());
        espacio.setCorreoContacto(espacioActualizado.getCorreoContacto());
        espacio.setAforo(espacioActualizado.getAforo());
        espacio.setHoraAbre(espacioActualizado.getHoraAbre());
        espacio.setHoraCierra(espacioActualizado.getHoraCierra());

        if (espacioActualizado.getTipoEspacio() != null && espacioActualizado.getTipoEspacio().getNombre() != null) {
            Optional<TipoEspacio> opttipo = tipoEspacioRepository.findById(espacioActualizado.getTipoEspacio().getId());
            if (opttipo.isPresent()) {
                TipoEspacio tipo = opttipo.get();
                espacio.setTipoEspacio(tipo);
            }
        }

        espaciosRepository.save(espacio);
        return ResponseEntity.ok("Actualizado correctamente");
    }


    // GUARDAR NUEVO O EDITADO
    @PostMapping("/guardar")
    public String guardarServicio(@ModelAttribute("espacio") EspaciosDeportivos espacio) {
        espaciosRepository.save(espacio);
        return "redirect:admin/servicios";
    }

    @DeleteMapping("/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminarServicio(@PathVariable("id") int id) {
        if (!espaciosRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        espaciosRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/guardarservicio")
    public String guardarServicio(@ModelAttribute("servicioDTO") ServicioDTO servicioDTO, @RequestParam("archivo") MultipartFile file ){
        if(file.isEmpty()) {
            return "admin/agregarservicio";
        }

        String fileName = file.getOriginalFilename();

        if (fileName.contains("..")){
            return "admin/agregarservicio";
        }

        try {
            ListaFotos listaFotos = new ListaFotos();
            listaFotosRepository.save(listaFotos);
            Fotos foto = new Fotos();
            foto.setFoto(file.getBytes());
            foto.setFotoNombre(fileName);
            foto.setFotoTipoArchivo(file.getContentType());
            foto.setListaFotos(listaFotos);
            fotosRepository.save(foto);
            EspaciosDeportivos espaciosDeportivos = servicioDTO.getEspacio();
            espaciosDeportivos.setListaFotos(listaFotos);
            if(espaciosDeportivos.getTipoEspacio().getId() == 1){
                Piscinas piscina = servicioDTO.getPiscina();
                espaciosDeportivosRepository.save(espaciosDeportivos);
                piscina.setIdEspacio(espaciosDeportivos.getId());
                piscinaRepository.save(piscina);
            } else if (espaciosDeportivos.getTipoEspacio().getId() == 2) {
                CanchasFutbol canchasFutbol = servicioDTO.getCancha();
                espaciosDeportivosRepository.save(espaciosDeportivos);
                canchasFutbol.setIdEspacio(espaciosDeportivos.getId());
                canchasFutbolRepository.save(canchasFutbol);
            } else if (espaciosDeportivos.getTipoEspacio().getId() == 3) {
                PistasAtletismo pistasAtletismo = servicioDTO.getPista();
                espaciosDeportivosRepository.save(espaciosDeportivos);
                pistasAtletismo.setIdEspacio(espaciosDeportivos.getId());
                pistasAtletismoRepository.save(pistasAtletismo);
            } else if (espaciosDeportivos.getTipoEspacio().getId() == 4) {
                Estadios estadios = servicioDTO.getEstadios();
                espaciosDeportivosRepository.save(espaciosDeportivos);
                estadios.setIdEspacio(espaciosDeportivos.getId());
                estadiosRepository.save(estadios);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin";
        }
        return "redirect:/admin";
    }

//reservas uwu

    @GetMapping("/reservas")
    public String Reservas(Model model){
        return "admin/reservas";
    }

    @GetMapping("/servicios/exportar-reporte-pdf")
    public void exportarReportePdf(@RequestParam("id") int idEspacio, HttpServletResponse response) throws Exception {
        EspaciosDeportivos espacio = espaciosRepository.findById(idEspacio).orElse(null);
        if (espacio == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Espacio no encontrado");
            return;
        }

        String nombreServicio = espacio.getNombre();

        // Obtener imagen
        byte[] imagen = null;
        if (espacio.getListaFotos() != null) {
            List<Fotos> fotos = fotosRepository.findByListaFotosId(espacio.getListaFotos().getId());
            if (!fotos.isEmpty()) {
                imagen = fotos.get(0).getFoto();
            }
        }

        // Configurar PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_servicio_" + idEspacio + ".pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Logo
        String imagePath = "src/main/resources/static/images/logo-sanMiguel.png";
        ImageData imageData = ImageDataFactory.create(imagePath);
        Image logo = new Image(imageData);
        logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
        logo.setWidth(60);
        document.add(logo);

        // Título
        Paragraph titulo = new Paragraph("Reporte de Servicio Deportivo")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(16);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph(nombreServicio)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(13);
        document.add(subtitulo);

        // Imagen del espacio
        if (imagen != null) {
            Image img = new Image(ImageDataFactory.create(imagen))
                    .scaleToFit(200, 200)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(img);
            document.add(new Paragraph("\n"));
        }

        // Datos del servicio
        document.add(new Paragraph("Tipo: " + espacio.getTipoEspacio().getNombre()));
        document.add(new Paragraph("Ubicación: " + espacio.getUbicacion()));
        document.add(new Paragraph("Horario: " + espacio.getHoraAbre() + " - " + espacio.getHoraCierra()));
        document.add(new Paragraph("Correo: " + espacio.getCorreoContacto()));
        document.add(new Paragraph("\n"));

        // Tabla de reservas
        List<Reservas> reservas = reservaRepository.findByEspacioDeportivoId(idEspacio);
        if (!reservas.isEmpty()) {
            DeviceRgb celesteOscuro = new DeviceRgb(36, 118, 141);

            Table table = new Table(5);
            table.setWidth(UnitValue.createPercentValue(100)); // ✅ Alternativa válida en iText 7

            table.setHorizontalAlignment(HorizontalAlignment.CENTER);

            // Encabezados
            table.addHeaderCell(new Cell().add(new Paragraph("Usuario"))
                    .setBackgroundColor(celesteOscuro)
                    .setFontColor(ColorConstants.WHITE)  // <- Letras blancas
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setPadding(5));

            table.addHeaderCell(new Cell().add(new Paragraph("Fecha"))
                    .setBackgroundColor(celesteOscuro)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setPadding(5));

            table.addHeaderCell(new Cell().add(new Paragraph("Horario"))
                    .setBackgroundColor(celesteOscuro)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setPadding(5));

            table.addHeaderCell(new Cell().add(new Paragraph("Medio Pago"))
                    .setBackgroundColor(celesteOscuro)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setPadding(5));

            table.addHeaderCell(new Cell().add(new Paragraph("Monto"))
                    .setBackgroundColor(celesteOscuro)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setPadding(5));

            // Filas de datos
            for (Reservas r : reservas) {
                table.addCell(new Cell().add(new Paragraph(r.getUsuario().getNombres() + " " + r.getUsuario().getApellidos())).setTextAlignment(TextAlignment.CENTER).setPadding(4));
                table.addCell(new Cell().add(new Paragraph(r.getFechaReserva().toString())).setTextAlignment(TextAlignment.CENTER).setPadding(4));
                table.addCell(new Cell().add(new Paragraph(r.getHorario().getHoraInicio() + " - " + r.getHorario().getHoraFin())).setTextAlignment(TextAlignment.CENTER).setPadding(4));
                table.addCell(new Cell().add(new Paragraph(r.getPago().getMedioPago().getNombre())).setTextAlignment(TextAlignment.CENTER).setPadding(4));
                table.addCell(new Cell().add(new Paragraph("S/ " + r.getPago().getCantidad())).setTextAlignment(TextAlignment.CENTER).setPadding(4));
            }

            document.add(new Paragraph("Reservas realizadas:").setBold());
            document.add(table);
        } else {
            document.add(new Paragraph("No se han registrado reservas para este servicio."));
        }

        document.close();
    }

    @GetMapping("/servicios/exportar-reporte-excel")
    public void exportarReporteExcel(@RequestParam("id") int idEspacio, HttpServletResponse response) throws Exception {
        EspaciosDeportivos espacio = espaciosRepository.findById(idEspacio).orElse(null);
        if (espacio == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Espacio no encontrado");
            return;
        }

        List<Reservas> reservas = reservaRepository.findByEspacioDeportivoId(idEspacio);

        // Crear workbook y hoja
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reservas");

        // Estilo de encabezado
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

        // Crear fila de encabezado
        Row header = sheet.createRow(0);
        String[] columnas = {"Usuario", "Fecha", "Horario", "Medio Pago", "Monto"};

        for (int i = 0; i < columnas.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        // Filas de contenido
        int fila = 1;
        for (Reservas r : reservas) {
            Row dataRow = sheet.createRow(fila++);
            dataRow.createCell(0).setCellValue(r.getUsuario().getNombres() + " " + r.getUsuario().getApellidos());
            dataRow.createCell(1).setCellValue(r.getFechaReserva().toString());
            dataRow.createCell(2).setCellValue(r.getHorario().getHoraInicio() + " - " + r.getHorario().getHoraFin());
            dataRow.createCell(3).setCellValue(r.getPago().getMedioPago().getNombre());
            dataRow.createCell(4).setCellValue("S/ " + r.getPago().getCantidad());
        }

        // Autoajustar columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Configurar descarga
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_servicio_" + idEspacio + ".xlsx");

        // Escribir archivo
        workbook.write(response.getOutputStream());
        workbook.close();
    }


}