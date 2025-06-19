package com.example.gtics_ta.Services;

import com.example.gtics_ta.DTO.ChatMessageDTO;
import com.example.gtics_ta.DTO.HorariosConsultaDTO;
import com.example.gtics_ta.Entity.EspaciosDeportivos;
import com.example.gtics_ta.Repository.EspaciosDeportivosRepository;
import com.example.gtics_ta.Repository.HorariosRepository;
import com.example.gtics_ta.Repository.ReservasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OpenAiService {

    @Autowired
    EspaciosDeportivosRepository espaciosDeportivosRepository;

    @Autowired
    HorariosRepository horariosRepository;

    @Autowired
    ReservasRepository reservasRepository;

    public String generarRespuesta(ChatMessageDTO mensajeDTO) {
        String mensaje = mensajeDTO.getMensajeUsuario().toLowerCase();

        // --- DISPONIBILIDAD DE LA PISCINA ---
        if (mensaje.contains("disponibilidad") || (mensaje.contains("está") && mensaje.contains("disponible"))) {
            Optional<EspaciosDeportivos> piscinaOpt = espaciosDeportivosRepository.findAll()
                    .stream()
                    .filter(e -> e.getNombre().toLowerCase().contains("piscina"))
                    .findFirst();

            if (piscinaOpt.isPresent()) {
                EspaciosDeportivos piscina = piscinaOpt.get();
                List<HorariosConsultaDTO> horarios = horariosRepository.obtenerHorariosConsulta(LocalDate.now(), piscina.getId());

                StringBuilder respuesta = new StringBuilder("Horarios disponibles hoy para la " + piscina.getNombre() + ":\n");
                boolean algunoLibre = false;
                for (HorariosConsultaDTO horario : horarios) {
                    if (horario.getReservado() == 0) {
                        respuesta.append("- De ").append(horario.getHoraInicio()).append(" a ").append(horario.getHoraFin()).append("\n");
                        algunoLibre = true;
                    }
                }

                return algunoLibre ? respuesta.toString() : "La piscina no tiene horarios disponibles hoy.";
            } else {
                return "No se encontró información sobre la piscina.";
            }
        }

        // --- PREGUNTAS SOBRE EL PRECIO ---
        if ((mensaje.contains("cuánto cuesta") || mensaje.contains("precio")) && mensaje.contains("piscina")) {
            Optional<EspaciosDeportivos> piscinaOpt = espaciosDeportivosRepository.findAll()
                    .stream()
                    .filter(e -> e.getNombre().toLowerCase().contains("piscina"))
                    .findFirst();

            if (piscinaOpt.isPresent()) {
                float costo = piscinaOpt.get().getCostoHorario();
                return "El costo por hora para la piscina es S/." + costo;
            } else {
                return "No se encontró información de precios para la piscina.";
            }
        }

        // --- CANTIDAD DE RESERVAS ---
        if (mensaje.contains("cuántas reservas") || mensaje.contains("reservas totales")) {
            Long total = reservasRepository.contarTotalReservas();
            return "Actualmente se han registrado un total de " + total + " reservas.";
        }

        // --- PREGUNTAS SOBRE HORARIOS ---
        if (mensaje.contains("qué horarios") || mensaje.contains("horarios disponibles")) {
            Optional<EspaciosDeportivos> espacioOpt = espaciosDeportivosRepository.findAll()
                    .stream()
                    .filter(e -> e.getNombre().toLowerCase().contains("piscina"))  // Aquí puedes mejorar la lógica para detectar otros espacios
                    .findFirst();

            if (espacioOpt.isPresent()) {
                List<HorariosConsultaDTO> horarios = horariosRepository.obtenerHorariosConsulta(LocalDate.now(), espacioOpt.get().getId());
                StringBuilder respuesta = new StringBuilder("Horarios disponibles para hoy:\n");
                boolean algunoLibre = false;
                for (HorariosConsultaDTO horario : horarios) {
                    if (horario.getReservado() == 0) {
                        respuesta.append("- De ").append(horario.getHoraInicio()).append(" a ").append(horario.getHoraFin()).append("\n");
                        algunoLibre = true;
                    }
                }

                return algunoLibre ? respuesta.toString() : "No hay horarios disponibles para hoy.";
            } else {
                return "No se encontró ningún espacio deportivo.";
            }
        }

        // --- PAGOS ---
        if (mensaje.contains("pagos") || mensaje.contains("cobros") || mensaje.contains("cuánto se ha recaudado")) {
            List<Object[]> resumen = reservasRepository.reporteMensualUltimos3Meses();
            if (!resumen.isEmpty()) {
                Object[] ultimo = resumen.get(resumen.size() - 1);
                Double total = (Double) ultimo[1];
                return "En el último mes se ha recaudado aproximadamente S/." + total;
            } else {
                return "No se han registrado pagos en los últimos meses.";
            }
        }

        // --- RESPUESTA POR DEFECTO ---
        return "No entendí tu pregunta. Puedes preguntarme sobre disponibilidad, horarios, precios, reservas o pagos.";
    }

}