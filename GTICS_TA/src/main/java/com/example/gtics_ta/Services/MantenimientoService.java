package com.example.gtics_ta.Services;

import com.example.gtics_ta.Entity.*;
import com.example.gtics_ta.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MantenimientoService {

    @Autowired
    private MantenimientoRepository mantenimientoRepository;

    @Autowired
    private ReservasRepository reservasRepository;

    @Autowired
    private NotificacionesRepository notificacionesRepository;

    @Autowired
    private EspaciosDeportivosRepository espaciosDeportivosRepository;

    /**
     * Programa un nuevo mantenimiento y cancela las reservas si es necesario
     */
    @Transactional
    public Mantenimiento programarMantenimiento(
            Integer espacioId,
            String tipo,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            String responsable,
            String contactoEncargado,
            String descripcion,
            String prioridad,
            boolean suspenderServicio,
            Usuario creadoPor) {

        // Buscar el espacio deportivo
        EspaciosDeportivos espacio = espaciosDeportivosRepository.findById(espacioId)
                .orElseThrow(() -> new RuntimeException("Espacio deportivo no encontrado"));

        // Crear el mantenimiento
        Mantenimiento mantenimiento = new Mantenimiento();
        mantenimiento.setEspacio(espacio);
        mantenimiento.setTipoMantenimiento(mapearTipoMantenimiento(tipo));
        mantenimiento.setTitulo(generarTitulo(tipo, espacio.getNombre()));
        mantenimiento.setDescripcion(descripcion);
        mantenimiento.setFechaInicio(fecha);
        mantenimiento.setFechaFin(fecha); // Por ahora es el mismo día
        mantenimiento.setHoraInicio(Time.valueOf(horaInicio));
        mantenimiento.setHoraFin(Time.valueOf(horaFin));
        mantenimiento.setEmpresaEncargada(responsable);
        mantenimiento.setPrioridad(mapearPrioridad(prioridad));
        mantenimiento.setRequiereCierreTotal(suspenderServicio);
        mantenimiento.setEstadoMantenimiento(Mantenimiento.EstadoMantenimiento.PROGRAMADO);
        mantenimiento.setCreadoPor(creadoPor);
        mantenimiento.setFechaCreacion(new Timestamp(System.currentTimeMillis()));

        // Guardar el mantenimiento
        mantenimiento = mantenimientoRepository.save(mantenimiento);

        // Si se suspende el servicio, cancelar reservas y enviar notificaciones
        if (suspenderServicio) {
            int reservasCanceladas = cancelarReservasYNotificar(espacio, fecha, mantenimiento);
            mantenimiento.setReservasCanceladas(reservasCanceladas);
            mantenimiento.setNotificacionesEnviadas(true);
            mantenimientoRepository.save(mantenimiento);
        }

        return mantenimiento;
    }

    /**
     * Cancela las reservas del día y envía notificaciones a los usuarios
     * Considera las horas del mantenimiento para determinar qué reservas cancelar
     */
    @Transactional
    public int cancelarReservasYNotificar(EspaciosDeportivos espacio, LocalDate fecha, Mantenimiento mantenimiento) {
        // Buscar reservas del día para el espacio
        List<Reservas> reservasDelDia = reservasRepository.findByEspacioDeportivoAndFechaReserva(espacio, fecha);

        int reservasCanceladas = 0;

        for (Reservas reserva : reservasDelDia) {
            // Solo cancelar si no está ya cancelada
            if (reserva.getEstadoReserva() != Reservas.EstadoReserva.CANCELADA_ADMIN &&
                reserva.getEstadoReserva() != Reservas.EstadoReserva.CANCELADA_USUARIO &&
                reserva.getEstadoReserva() != Reservas.EstadoReserva.CANCELADA_MANTENIMIENTO) {

                boolean debeCancel = false;

                // Si el mantenimiento requiere cierre total, cancelar todas las reservas del día
                if (mantenimiento.getRequiereCierreTotal()) {
                    debeCancel = true;
                } else {
                    // Si no requiere cierre total, verificar si hay conflicto de horarios
                    if (reserva.getHorario() != null) {
                        LocalTime horaInicioReserva = reserva.getHorario().getHoraInicio().toLocalTime();
                        LocalTime horaFinReserva = reserva.getHorario().getHoraFin().toLocalTime();
                        LocalTime horaInicioMantenimiento = mantenimiento.getHoraInicio().toLocalTime();
                        LocalTime horaFinMantenimiento = mantenimiento.getHoraFin().toLocalTime();

                        // Verificar si hay solapamiento de horarios
                        debeCancel = !(horaFinReserva.isBefore(horaInicioMantenimiento) ||
                                      horaInicioReserva.isAfter(horaFinMantenimiento));
                    }
                }

                if (debeCancel) {
                    // Cambiar estado a cancelada por mantenimiento
                    reserva.setEstadoReserva(Reservas.EstadoReserva.CANCELADA_MANTENIMIENTO);
                    reserva.setReembolsoProcesado(true);
                    reserva.setMotivoCancelacion("Cancelada por mantenimiento programado");
                    reserva.setFechaCancelacion(new Timestamp(System.currentTimeMillis()));
                    reserva.setMantenimiento(mantenimiento);
                    reservasRepository.save(reserva);
                    reservasCanceladas++;

                    // Crear notificación para el usuario
                    crearNotificacionCancelacion(reserva, mantenimiento);
                }
            }
        }

        return reservasCanceladas;
    }

    /**
     * Crea una notificación de cancelación por mantenimiento
     */
    private void crearNotificacionCancelacion(Reservas reserva, Mantenimiento mantenimiento) {
        String titulo = "Reserva Cancelada por Mantenimiento";

        DateTimeFormatter fechaFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String fechaFormateada = reserva.getFechaReserva().format(fechaFormatter);

        String horarioReserva = "No especificado";
        if (reserva.getHorario() != null) {
            horarioReserva = reserva.getHorario().getHoraInicio().toLocalTime().format(horaFormatter) +
                           " - " + reserva.getHorario().getHoraFin().toLocalTime().format(horaFormatter);
        }

        String horarioMantenimiento = mantenimiento.getHoraInicio().toLocalTime().format(horaFormatter) +
                                    " - " + mantenimiento.getHoraFin().toLocalTime().format(horaFormatter);

        String mensaje = String.format(
            "Estimado/a %s,\n\n" +
            "Lamentamos informarle que su reserva ha sido cancelada debido a mantenimiento programado:\n\n" +
            "*Lugar: %s\n" +
            "*Fecha de reserva: %s\n" +
            "*Horario de reserva: %s\n" +
            "*Tipo de mantenimiento: %s\n" +
            "*Descripción: %s\n\n" +
            "El mantenimiento está programado para el %s de %s.\n\n" +
            "*REEMBOLSO: Se procesará automáticamente el reembolso completo de su pago.\n\n" +
            "Disculpe las molestias ocasionadas. Puede realizar una nueva reserva para otra fecha disponible.\n\n" +
            "Para consultas, puede contactarnos o visitar nuestras oficinas.\n\n" +
            "Atentamente,\n" +
            "Administración - Municipalidad de San Miguel",
            reserva.getUsuario().getNombres(),
            reserva.getEspacioDeportivo().getNombre(),
            fechaFormateada,
            horarioReserva,
            mantenimiento.getTipoMantenimiento().toString().toLowerCase().replace("_", " "),
            mantenimiento.getDescripcion(),
            fechaFormateada,
            horarioMantenimiento
        );

        Notificaciones notificacion = new Notificaciones(
            reserva.getUsuario(),
            Notificaciones.TipoNotificacion.CANCELACION_RESERVA,
            titulo,
            mensaje,
            reserva
        );

        notificacion.setMantenimiento(mantenimiento);
        notificacionesRepository.save(notificacion);
    }

    /**
     * Mapea el tipo de mantenimiento desde string a enum
     */
    private Mantenimiento.TipoMantenimiento mapearTipoMantenimiento(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "preventivo" -> Mantenimiento.TipoMantenimiento.PREVENTIVO;
            case "correctivo" -> Mantenimiento.TipoMantenimiento.CORRECTIVO;
            case "limpieza" -> Mantenimiento.TipoMantenimiento.LIMPIEZA_PROFUNDA;
            case "inspeccion" -> Mantenimiento.TipoMantenimiento.PREVENTIVO; // Inspección como preventivo
            case "reparacion" -> Mantenimiento.TipoMantenimiento.CORRECTIVO; // Reparación como correctivo
            default -> Mantenimiento.TipoMantenimiento.PREVENTIVO;
        };
    }

    /**
     * Mapea la prioridad desde string a enum
     */
    private Mantenimiento.Prioridad mapearPrioridad(String prioridad) {
        return switch (prioridad.toLowerCase()) {
            case "baja" -> Mantenimiento.Prioridad.BAJA;
            case "media" -> Mantenimiento.Prioridad.MEDIA;
            case "alta" -> Mantenimiento.Prioridad.ALTA;
            case "urgente" -> Mantenimiento.Prioridad.CRITICA;
            default -> Mantenimiento.Prioridad.MEDIA;
        };
    }

    /**
     * Genera un título descriptivo para el mantenimiento
     */
    private String generarTitulo(String tipo, String nombreEspacio) {
        String tipoTexto = switch (tipo.toLowerCase()) {
            case "preventivo" -> "Mantenimiento Preventivo";
            case "correctivo" -> "Mantenimiento Correctivo";
            case "limpieza" -> "Limpieza Profunda";
            case "inspeccion" -> "Inspección";
            case "reparacion" -> "Reparación";
            default -> "Mantenimiento";
        };
        
        return tipoTexto + " - " + nombreEspacio;
    }

    /**
     * Obtiene todos los mantenimientos activos
     */
    public List<Mantenimiento> obtenerMantenimientosActivos() {
        return mantenimientoRepository.findMantenimientosActivos();
    }

    /**
     * Obtiene mantenimientos por espacio
     */
    public List<Mantenimiento> obtenerMantenimientosPorEspacio(EspaciosDeportivos espacio) {
        return mantenimientoRepository.findByEspacioOrderByFechaInicioDesc(espacio);
    }
}
