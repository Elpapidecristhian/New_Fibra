package com.example.gtics_ta.Services;

import com.example.gtics_ta.DTO.ChatMessageDTO;
import com.example.gtics_ta.DTO.HorariosConsultaDTO;
import com.example.gtics_ta.Entity.EspaciosDeportivos;
import com.example.gtics_ta.Repository.EspaciosDeportivosRepository;
import com.example.gtics_ta.Repository.HorariosRepository;
import com.example.gtics_ta.Repository.ReservasRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

    @Value("${openai.api.key}")
    private String apiKey;

    public String generarRespuesta(ChatMessageDTO mensajeDTO) {
        String mensaje = mensajeDTO.getMensajeUsuario().toLowerCase();

        try {
            // PREGUNTA DE PRECIO
            if (mensaje.contains("precio") || mensaje.contains("cuesta")) {
                Optional<EspaciosDeportivos> espacioOpt = espaciosDeportivosRepository.findAll()
                        .stream()
                        .filter(e -> mensaje.contains(e.getNombre().toLowerCase()))
                        .findFirst();

                if (espacioOpt.isPresent()) {
                    float costo = espacioOpt.get().getCostoHorario();
                    String prompt = """
                            El usuario preguntó: "%s".
                            El precio por hora del espacio deportivo llamado '%s' es S/%.2f.
                            Redáctale una respuesta clara y amable con esa información.
                            """.formatted(mensajeDTO.getMensajeUsuario(), espacioOpt.get().getNombre(), costo);
                    return llamarAGpt(prompt);
                } else {
                    return "No se encontró información sobre el espacio deportivo indicado.";
                }
            }

            // PREGUNTA DE HORARIOS
            if (mensaje.contains("horarios") || mensaje.contains("disponibilidad")) {
                Optional<EspaciosDeportivos> espacioOpt = espaciosDeportivosRepository.findAll()
                        .stream()
                        .filter(e -> mensaje.contains(e.getNombre().toLowerCase()))
                        .findFirst();

                if (espacioOpt.isPresent()) {
                    List<HorariosConsultaDTO> horarios = horariosRepository.obtenerHorariosConsulta(LocalDate.now(), espacioOpt.get().getId());
                    StringBuilder disponibles = new StringBuilder();

                    for (HorariosConsultaDTO h : horarios) {
                        if (h.getReservado() == 0) {
                            disponibles.append("- ").append(h.getHoraInicio()).append(" a ").append(h.getHoraFin()).append("\n");
                        }
                    }

                    if (disponibles.length() == 0) {
                        return "No hay horarios disponibles hoy para el espacio solicitado.";
                    }

                    String prompt = """
                            El usuario preguntó: "%s".
                            Hoy hay disponibilidad en los siguientes horarios para '%s':
                            %s
                            Redacta la respuesta con tono informativo y cordial.
                            """.formatted(mensajeDTO.getMensajeUsuario(), espacioOpt.get().getNombre(), disponibles.toString());
                    return llamarAGpt(prompt);
                } else {
                    return "No se encontró información sobre el espacio deportivo indicado.";
                }
            }

            // PREGUNTA DE RESERVAS
            if (mensaje.contains("cuántas reservas") || mensaje.contains("reservas totales")) {
                long total = reservasRepository.contarTotalReservas();
                String prompt = """
                        El usuario preguntó: "%s".
                        Hay %d reservas registradas en el sistema.
                        Redacta una respuesta cordial y clara para informar.
                        """.formatted(mensajeDTO.getMensajeUsuario(), total);
                return llamarAGpt(prompt);
            }

            // PREGUNTA DE PAGOS
            if (mensaje.contains("pagos") || mensaje.contains("cobros") || mensaje.contains("recaudado")) {
                List<Object[]> resumen = reservasRepository.reporteMensualUltimos3Meses();
                if (!resumen.isEmpty()) {
                    Object[] ultimo = resumen.get(resumen.size() - 1);
                    Double total = ultimo[1] != null ? (Double) ultimo[1] : 0.0;
                    if (total == 0.0) {
                        return "No se han registrado pagos en el último mes.";
                    }
                    String prompt = """
                            El usuario preguntó: "%s".
                            En el último mes se ha recaudado S/.%.2f.
                            Redacta una respuesta clara y amable.
                            """.formatted(mensajeDTO.getMensajeUsuario(), total);
                    return llamarAGpt(prompt);
                } else {
                    return "No se han registrado pagos recientemente.";
                }
            }

            // PREGUNTA DE DEVOLUCIONES
            if (mensaje.contains("devolución") || mensaje.contains("reembolso")) {
                String prompt = """
                        El usuario preguntó: "%s".
                        Responde que el administrador se pondrá en contacto para atender su solicitud de devolución.
                        Redacta con un tono empático y profesional.
                        """.formatted(mensajeDTO.getMensajeUsuario());
                return llamarAGpt(prompt);
            }

            // RESPUESTA POR DEFECTO
            String fallbackPrompt = """
                    El usuario preguntó: "%s".
                    No tengo información específica para esta consulta. Redacta una respuesta cordial pidiéndole que sea más específico o que consulte sobre reservas, horarios, pagos, precios o devoluciones.
                    """.formatted(mensajeDTO.getMensajeUsuario());
            return llamarAGpt(fallbackPrompt);

        } catch (Exception e) {
            e.printStackTrace();
            return "Ocurrió un error al procesar tu pregunta. Intenta nuevamente más tarde.";
        }
    }

    private String llamarAGpt(String prompt) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode requestJson = mapper.createObjectNode();
            requestJson.put("model", "gpt-3.5-turbo");

            ArrayNode messages = mapper.createArrayNode();

            ObjectNode systemMsg = mapper.createObjectNode();
            systemMsg.put("role", "system");
            systemMsg.put("content", "Eres un asistente virtual del sistema de reservas deportivas de la Municipalidad de San Miguel. Responde con claridad, cordialidad y precisión.");
            messages.add(systemMsg);

            ObjectNode userMsg = mapper.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.add(userMsg);

            requestJson.set("messages", messages);

            String requestBody = mapper.writeValueAsString(requestJson);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());

            if (root.has("choices")) {
                JsonNode choices = root.get("choices");
                if (choices.isArray() && choices.size() > 0) {
                    JsonNode message = choices.get(0).get("message");
                    if (message != null && message.has("content")) {
                        return message.get("content").asText().trim();
                    }
                }
                return "GPT respondió, pero el mensaje estaba vacío.";
            } else if (root.has("error")) {
                String msgError = root.get("error").get("message").asText();
                return "Error de OpenAI: " + msgError;
            } else {
                return "Respuesta inesperada de la IA.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "No se pudo generar una respuesta en este momento. Motivo técnico: " + e.getMessage();
        }
    }
}
