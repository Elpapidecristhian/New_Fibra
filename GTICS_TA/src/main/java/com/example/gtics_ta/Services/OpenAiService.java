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
            if (mensaje.contains("precio") && mensaje.contains("piscina")) {
                Optional<EspaciosDeportivos> piscinaOpt = espaciosDeportivosRepository.findAll()
                        .stream()
                        .filter(e -> e.getNombre().toLowerCase().contains("piscina"))
                        .findFirst();

                if (piscinaOpt.isPresent()) {
                    float costo = piscinaOpt.get().getCostoHorario();
                    String prompt = """
                            El usuario preguntó: "%s".
                            El precio por hora para la piscina es S/%.2f.
                            Redáctale una respuesta clara y amigable para informarle.
                            """.formatted(mensajeDTO.getMensajeUsuario(), costo);
                    return llamarAGpt(prompt);
                } else {
                    return "No se encontró información sobre la piscina.";
                }
            }

            // PREGUNTA DE HORARIOS
            if (mensaje.contains("horarios") || mensaje.contains("disponibilidad")) {
                Optional<EspaciosDeportivos> piscinaOpt = espaciosDeportivosRepository.findAll()
                        .stream()
                        .filter(e -> e.getNombre().toLowerCase().contains("piscina"))
                        .findFirst();

                if (piscinaOpt.isPresent()) {
                    List<HorariosConsultaDTO> horarios = horariosRepository.obtenerHorariosConsulta(LocalDate.now(), piscinaOpt.get().getId());
                    StringBuilder disponibles = new StringBuilder();

                    for (HorariosConsultaDTO h : horarios) {
                        if (h.getReservado() == 0) {
                            disponibles.append("- ").append(h.getHoraInicio()).append(" a ").append(h.getHoraFin()).append("\n");
                        }
                    }

                    String prompt = """
                            El usuario preguntó: "%s".
                            Los horarios disponibles hoy para la piscina son:
                            %s
                            Redáctale una respuesta útil y cordial.
                            """.formatted(mensajeDTO.getMensajeUsuario(), disponibles.toString());

                    return llamarAGpt(prompt);
                } else {
                    return "No se encontró información de horarios para la piscina.";
                }
            }

            // PREGUNTA DE RESERVAS
            if (mensaje.contains("cuántas reservas") || mensaje.contains("reservas totales")) {
                long total = reservasRepository.contarTotalReservas();

                String prompt = """
                        El usuario preguntó: "%s".
                        Actualmente hay %d reservas registradas en el sistema.
                        Redáctalo de forma clara y natural.
                        """.formatted(mensajeDTO.getMensajeUsuario(), total);

                return llamarAGpt(prompt);
            }

            // PREGUNTA DE PAGOS
            if (mensaje.contains("pagos") || mensaje.contains("cobros") || mensaje.contains("recaudado")) {
                List<Object[]> resumen = reservasRepository.reporteMensualUltimos3Meses();
                if (!resumen.isEmpty()) {
                    Object[] ultimo = resumen.get(resumen.size() - 1);
                    Double total = (Double) ultimo[1];
                    String prompt = """
                            El usuario preguntó: "%s".
                            En el último mes se ha recaudado un total aproximado de S/.%.2f.
                            Responde con esa información de forma clara y amable.
                            """.formatted(mensajeDTO.getMensajeUsuario(), total);
                    return llamarAGpt(prompt);
                } else {
                    return "No se han registrado pagos recientemente.";
                }
            }

            // RESPUESTA POR DEFECTO
            String fallbackPrompt = """
                    El usuario preguntó: "%s".
                    No tengo datos específicos para su pregunta, pero puedes redactar una respuesta cordial pidiéndole más detalles o explicando los temas que puede consultar.
                    """.formatted(mensajeDTO.getMensajeUsuario());

            return llamarAGpt(fallbackPrompt);

        } catch (Exception e) {
            e.printStackTrace();
            return "Ocurrió un error al procesar tu pregunta. Intenta nuevamente más tarde.";
        }
    }
    private String llamarAGpt(String prompt) {
        try {
            // Crea el cuerpo JSON con ObjectMapper (evita errores de formato)
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

            System.out.println("🧠 Respuesta OpenAI JSON:");
            System.out.println(response.body());

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
