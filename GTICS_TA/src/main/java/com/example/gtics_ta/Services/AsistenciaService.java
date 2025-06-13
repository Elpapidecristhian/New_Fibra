package com.example.gtics_ta.Services;

import com.example.gtics_ta.Entity.*;
import com.example.gtics_ta.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AsistenciaService {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private HorariosCoordinadorRepository horariosCoordinadorRepository;

    @Autowired
    private EspaciosDeportivosRepository espaciosDeportivosRepository;

    /**
     * Verifica si el usuario está dentro del radio de algún espacio deportivo
     */
    public boolean verificarUbicacionEnEspacio(BigDecimal latitud, BigDecimal longitud) {
        List<EspaciosDeportivos> espacios = espaciosDeportivosRepository.findAll();
        
        for (EspaciosDeportivos espacio : espacios) {
            if (espacio.getLatitud() != null && espacio.getLongitud() != null) {
                double distancia = calcularDistancia(
                    latitud.doubleValue(), 
                    longitud.doubleValue(),
                    espacio.getLatitud().doubleValue(), 
                    espacio.getLongitud().doubleValue()
                );
                
                // Verificar si está dentro del radio (por defecto 100m)
                int radioPermitido = espacio.getRadioCobertura() != null ? espacio.getRadioCobertura() : 100;
                if (distancia <= radioPermitido) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Registra la entrada de un coordinador
     */
    public Asistencia registrarEntrada(Usuario coordinador, BigDecimal latitud, BigDecimal longitud) {
        // Verificar ubicación
        if (!verificarUbicacionEnEspacio(latitud, longitud)) {
            throw new RuntimeException("No estás dentro del área permitida para registrar asistencia");
        }

        // Obtener horario del coordinador para hoy
        LocalDate hoy = LocalDate.now();
        Date fechaHoy = java.sql.Date.valueOf(hoy);
        
        Optional<HorariosCoordinador> horarioOpt = obtenerHorarioCoordinadorHoy(coordinador, fechaHoy);
        if (horarioOpt.isEmpty()) {
            throw new RuntimeException("No tienes horarios asignados para hoy");
        }

        HorariosCoordinador horario = horarioOpt.get();

        // Verificar si ya existe asistencia para hoy
        Optional<Asistencia> asistenciaExistente = asistenciaRepository.findByHorariosCoordinadorAndFecha(horario, fechaHoy);
        if (asistenciaExistente.isPresent() && asistenciaExistente.get().getHoraEntrada() != null) {
            throw new RuntimeException("Ya has registrado tu entrada para hoy");
        }

        // Crear o actualizar asistencia
        Asistencia asistencia = asistenciaExistente.orElse(new Asistencia());
        asistencia.setHorariosCoordinador(horario);
        asistencia.setFecha(fechaHoy);
        asistencia.setHoraEntrada(Time.valueOf(LocalTime.now()));
        asistencia.setFechaRegistro(new Timestamp(System.currentTimeMillis()));

        // Calcular si llegó tarde
        LocalTime horaActual = LocalTime.now();
        LocalTime horaEsperada = horario.getHoraEntrada().toLocalTime();
        
        if (horaActual.isAfter(horaEsperada)) {
            long minutosRetraso = ChronoUnit.MINUTES.between(horaEsperada, horaActual);
            asistencia.setMinutosRetraso((int) minutosRetraso);
            asistencia.setEstadoAsistencia(Asistencia.EstadoAsistencia.TARDE);
        } else {
            asistencia.setMinutosRetraso(0);
            asistencia.setEstadoAsistencia(Asistencia.EstadoAsistencia.A_TIEMPO);
        }

        return asistenciaRepository.save(asistencia);
    }

    /**
     * Registra la salida de un coordinador
     */
    public Asistencia registrarSalida(Usuario coordinador, BigDecimal latitud, BigDecimal longitud) {
        // Verificar ubicación
        if (!verificarUbicacionEnEspacio(latitud, longitud)) {
            throw new RuntimeException("No estás dentro del área permitida para registrar asistencia");
        }

        // Obtener horario del coordinador para hoy
        LocalDate hoy = LocalDate.now();
        Date fechaHoy = java.sql.Date.valueOf(hoy);
        
        Optional<HorariosCoordinador> horarioOpt = obtenerHorarioCoordinadorHoy(coordinador, fechaHoy);
        if (horarioOpt.isEmpty()) {
            throw new RuntimeException("No tienes horarios asignados para hoy");
        }

        HorariosCoordinador horario = horarioOpt.get();

        // Verificar que existe asistencia con entrada registrada
        Optional<Asistencia> asistenciaOpt = asistenciaRepository.findByHorariosCoordinadorAndFecha(horario, fechaHoy);
        if (asistenciaOpt.isEmpty() || asistenciaOpt.get().getHoraEntrada() == null) {
            throw new RuntimeException("Debes registrar tu entrada antes de la salida");
        }

        Asistencia asistencia = asistenciaOpt.get();
        if (asistencia.getHoraSalida() != null) {
            throw new RuntimeException("Ya has registrado tu salida para hoy");
        }

        // Registrar salida
        LocalTime horaSalida = LocalTime.now();
        asistencia.setHoraSalida(Time.valueOf(horaSalida));

        // Calcular horas trabajadas
        LocalTime horaEntrada = asistencia.getHoraEntrada().toLocalTime();
        long minutosTrabajos = ChronoUnit.MINUTES.between(horaEntrada, horaSalida);
        long horas = minutosTrabajos / 60;
        long minutos = minutosTrabajos % 60;
        asistencia.setHorasTrabajadas(String.format("%02d:%02d", horas, minutos));

        return asistenciaRepository.save(asistencia);
    }

    /**
     * Obtiene el horario de un coordinador para una fecha específica
     */
    public Optional<HorariosCoordinador> obtenerHorarioCoordinadorHoy(Usuario coordinador, Date fecha) {
        List<HorariosCoordinador> horarios = horariosCoordinadorRepository.findByUsuarioAndFecha(coordinador, fecha);
        return horarios.isEmpty() ? Optional.empty() : Optional.of(horarios.get(0));
    }

    /**
     * Calcula la distancia entre dos puntos geográficos usando la fórmula de Haversine
     */
    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la Tierra en kilómetros
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // Convertir a metros
        
        return distance;
    }

    /**
     * Obtiene la asistencia del día actual para un coordinador
     */
    public Optional<Asistencia> obtenerAsistenciaHoy(Usuario coordinador) {
        LocalDate hoy = LocalDate.now();
        Date fechaHoy = java.sql.Date.valueOf(hoy);
        
        Optional<HorariosCoordinador> horarioOpt = obtenerHorarioCoordinadorHoy(coordinador, fechaHoy);
        if (horarioOpt.isEmpty()) {
            return Optional.empty();
        }

        return asistenciaRepository.findByHorariosCoordinadorAndFecha(horarioOpt.get(), fechaHoy);
    }

    /**
     * Obtiene los espacios deportivos con sus coordenadas para validación
     */
    public List<EspaciosDeportivos> obtenerEspaciosConCoordenadas() {
        return espaciosDeportivosRepository.findAll().stream()
            .filter(e -> e.getLatitud() != null && e.getLongitud() != null && e.isOperativo())
            .toList();
    }
}