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
     * Registra la entrada del coordinador
     */
    public Asistencia registrarEntrada(Usuario coordinador, BigDecimal latitud, BigDecimal longitud) {
        // Verificar ubicación
        if (!verificarUbicacionEnEspacio(latitud, longitud)) {
            throw new RuntimeException("No se encuentra dentro del área permitida de ningún espacio deportivo");
        }

        // Buscar horario del coordinador para hoy
        LocalDate hoy = LocalDate.now();
        Date fechaHoy = java.sql.Date.valueOf(hoy);
        
        Optional<HorariosCoordinador> horarioOpt = obtenerHorarioCoordinadorHoy(coordinador, fechaHoy);
        if (horarioOpt.isEmpty()) {
            throw new RuntimeException("No tiene horario asignado para el día de hoy");
        }

        HorariosCoordinador horario = horarioOpt.get();
        
        // Verificar si ya existe asistencia para hoy
        Optional<Asistencia> asistenciaExistente = asistenciaRepository.findByHorariosCoordinadorAndFecha(horario, fechaHoy);
        
        Asistencia asistencia;
        if (asistenciaExistente.isPresent()) {
            asistencia = asistenciaExistente.get();
            if (asistencia.getHoraEntrada() != null) {
                throw new RuntimeException("Ya ha registrado su entrada para el día de hoy");
            }
        } else {
            asistencia = new Asistencia();
            asistencia.setHorariosCoordinador(horario);
            asistencia.setFecha(fechaHoy);
            asistencia.setRegistradoPor(coordinador);
            asistencia.setFechaRegistro(new Timestamp(System.currentTimeMillis()));
        }

        // Registrar hora de entrada
        LocalTime horaActual = LocalTime.now();
        asistencia.setHoraEntrada(Time.valueOf(horaActual));
        
        // Calcular estado y minutos de retraso
        calcularEstadoAsistencia(asistencia, horario);
        
        return asistenciaRepository.save(asistencia);
    }

    /**
     * Registra la salida del coordinador
     */
    public Asistencia registrarSalida(Usuario coordinador, BigDecimal latitud, BigDecimal longitud) {
        // Verificar ubicación
        if (!verificarUbicacionEnEspacio(latitud, longitud)) {
            throw new RuntimeException("No se encuentra dentro del área permitida de ningún espacio deportivo");
        }

        // Buscar asistencia del día
        LocalDate hoy = LocalDate.now();
        Date fechaHoy = java.sql.Date.valueOf(hoy);
        
        Optional<HorariosCoordinador> horarioOpt = obtenerHorarioCoordinadorHoy(coordinador, fechaHoy);
        if (horarioOpt.isEmpty()) {
            throw new RuntimeException("No tiene horario asignado para el día de hoy");
        }

        HorariosCoordinador horario = horarioOpt.get();
        Optional<Asistencia> asistenciaOpt = asistenciaRepository.findByHorariosCoordinadorAndFecha(horario, fechaHoy);
        
        if (asistenciaOpt.isEmpty()) {
            throw new RuntimeException("Debe registrar primero su entrada");
        }

        Asistencia asistencia = asistenciaOpt.get();
        if (asistencia.getHoraEntrada() == null) {
            throw new RuntimeException("Debe registrar primero su entrada");
        }
        
        if (asistencia.getHoraSalida() != null) {
            throw new RuntimeException("Ya ha registrado su salida para el día de hoy");
        }

        // Registrar hora de salida
        LocalTime horaActual = LocalTime.now();
        asistencia.setHoraSalida(Time.valueOf(horaActual));
        
        // Calcular horas trabajadas
        calcularHorasTrabajadas(asistencia);
        
        // Recalcular estado con las horas trabajadas
        calcularEstadoAsistencia(asistencia, horario);
        
        return asistenciaRepository.save(asistencia);
    }

    /**
     * Obtiene el horario del coordinador para una fecha específica
     */
    private Optional<HorariosCoordinador> obtenerHorarioCoordinadorHoy(Usuario coordinador, Date fecha) {
        List<HorariosCoordinador> horarios = horariosCoordinadorRepository.findByUsuario(coordinador);
        
        LocalDate fechaBusqueda = ((java.sql.Date) fecha).toLocalDate();
        
        return horarios.stream()
            .filter(h -> {
                LocalDate fechaInicio = h.getFechaInicio().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                LocalDate fechaFin = h.getFechaFin().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                return !fechaBusqueda.isBefore(fechaInicio) && !fechaBusqueda.isAfter(fechaFin);
            })
            .findFirst();
    }

    /**
     * Calcula el estado de asistencia y minutos de retraso
     */
    private void calcularEstadoAsistencia(Asistencia asistencia, HorariosCoordinador horario) {
        if (asistencia.getHoraEntrada() == null) {
            asistencia.setEstadoAsistencia(Asistencia.EstadoAsistencia.FALTA);
            return;
        }

        LocalTime horaEntradaReal = asistencia.getHoraEntrada().toLocalTime();
        LocalTime horaEntradaEsperada = horario.getHoraEntrada().toLocalTime();
        
        // Tolerancia de 10 minutos
        LocalTime horaLimite = horaEntradaEsperada.plusMinutes(10);
        
        if (horaEntradaReal.isAfter(horaLimite)) {
            // Llegó tarde
            long minutosRetraso = ChronoUnit.MINUTES.between(horaEntradaEsperada, horaEntradaReal);
            asistencia.setMinutosRetraso((int) minutosRetraso);
            asistencia.setEstadoAsistencia(Asistencia.EstadoAsistencia.TARDE);
        } else {
            // Llegó a tiempo
            asistencia.setMinutosRetraso(0);
            asistencia.setEstadoAsistencia(Asistencia.EstadoAsistencia.A_TIEMPO);
        }
        
        // Si ya tiene salida, verificar horas trabajadas
        if (asistencia.getHoraSalida() != null) {
            verificarHorasMinimas(asistencia, horario);
        }
    }

    /**
     * Verifica si cumplió con las horas mínimas de trabajo
     */
    private void verificarHorasMinimas(Asistencia asistencia, HorariosCoordinador horario) {
        if (asistencia.getHoraEntrada() == null || asistencia.getHoraSalida() == null) {
            return;
        }

        LocalTime horaEntrada = asistencia.getHoraEntrada().toLocalTime();
        LocalTime horaSalida = asistencia.getHoraSalida().toLocalTime();
        LocalTime horaEntradaEsperada = horario.getHoraEntrada().toLocalTime();
        LocalTime horaSalidaEsperada = horario.getHoraSalida().toLocalTime();
        
        // Calcular horas trabajadas
        long minutosReales = ChronoUnit.MINUTES.between(horaEntrada, horaSalida);
        long minutosEsperados = ChronoUnit.MINUTES.between(horaEntradaEsperada, horaSalidaEsperada);
        
        // Tolerancia: debe trabajar al menos el 90% del tiempo esperado (4h 50min de 5h)
        long minutosMinimos = (long) (minutosEsperados * 0.9);
        
        if (minutosReales < minutosMinimos) {
            // No cumplió con las horas mínimas, mantener como TARDE
            if (asistencia.getEstadoAsistencia() == Asistencia.EstadoAsistencia.A_TIEMPO) {
                asistencia.setEstadoAsistencia(Asistencia.EstadoAsistencia.TARDE);
            }
        }
    }

    /**
     * Calcula las horas trabajadas
     */
    private void calcularHorasTrabajadas(Asistencia asistencia) {
        if (asistencia.getHoraEntrada() == null || asistencia.getHoraSalida() == null) {
            asistencia.setHorasTrabajadas("00:00:00");
            return;
        }

        LocalTime horaEntrada = asistencia.getHoraEntrada().toLocalTime();
        LocalTime horaSalida = asistencia.getHoraSalida().toLocalTime();
        
        long segundosTrabajados = ChronoUnit.SECONDS.between(horaEntrada, horaSalida);
        
        long horas = segundosTrabajados / 3600;
        long minutos = (segundosTrabajados % 3600) / 60;
        long segundos = segundosTrabajados % 60;
        
        String horasTrabajadas = String.format("%02d:%02d:%02d", horas, minutos, segundos);
        asistencia.setHorasTrabajadas(horasTrabajadas);
    }

    /**
     * Calcula la distancia entre dos puntos geográficos usando la fórmula de Haversine
     */
    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la Tierra en km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c * 1000; // Convertir a metros
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
