    package com.example.gtics_ta.Services;

    import com.example.gtics_ta.DTO.ChatMessageDTO;
    import com.example.gtics_ta.DTO.HorariosConsultaDTO;
    import com.example.gtics_ta.DTO.MensajeProcesadoDTO;
    import com.example.gtics_ta.Entity.EspaciosDeportivos;
    import com.example.gtics_ta.Entity.Pagos;
    import com.example.gtics_ta.Entity.Reservas;
    import com.example.gtics_ta.Repository.EspaciosDeportivosRepository;
    import com.example.gtics_ta.Repository.HorariosRepository;
    import com.example.gtics_ta.Repository.PagosRepository;
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
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;
    import java.util.stream.Collectors;

    @Service
    public class OpenAiService {

        @Value("${openai.api.key}")
        private String apiKey;

        private final ObjectMapper mapper = new ObjectMapper();

        @Autowired
        private EspaciosDeportivosRepository espaciosDeportivosRepository;

        @Autowired
        private ReservasRepository reservasRepository;

        @Autowired
        private HorariosRepository horariosRepository;

        @Autowired
        private PagosRepository pagosRepository;

        private String obtenerHorariosDesdeDto(String nombreEspacio, String fechaStr) {
            if (nombreEspacio == null || fechaStr == null) {
                return "Por favor indícame el nombre del espacio y la fecha para mostrar los horarios disponibles.";
            }

            List<EspaciosDeportivos> espacios = espaciosDeportivosRepository.findByNombreContaining(nombreEspacio);
            if (espacios.isEmpty()) {
                return "No se encontró ningún espacio con ese nombre.";
            }

            EspaciosDeportivos espacio = espacios.get(0);
            LocalDate fecha = LocalDate.parse(fechaStr);

            List<HorariosConsultaDTO> listaHorarios = horariosRepository.obtenerHorariosConsulta(fecha, espacio.getId());
            List<HorariosConsultaDTO> disponibles = listaHorarios.stream()
                    .filter(h -> h.getReservado() == null || h.getReservado() == 0)
                    .toList();

            if (disponibles.isEmpty()) {
                return "No hay horarios disponibles para " + espacio.getNombre() + " el " + fechaStr + ".";
            }

            StringBuilder sb = new StringBuilder("Horarios disponibles en ")
                    .append(espacio.getNombre()).append(" para el ").append(fechaStr).append(":\n");

            for (HorariosConsultaDTO h : disponibles) {
                sb.append("- ").append(h.getHoraInicio()).append(" - ").append(h.getHoraFin()).append("\n");
            }

            return sb.toString().trim();
        }
        private static final List<String> TIPOS_VALIDOS = List.of(
                "Piscina", "Gimnasio", "Estadio", "Cancha de Fútbol Grass", "Cancha de Loza", "Pista de Atletismo"
        );

        public String generarRespuesta(ChatMessageDTO mensajeDTO) {
            String mensajeUsuario = mensajeDTO.getMensajeUsuario();
            Integer usuarioId = mensajeDTO.getUsuarioId();

            try {
                MensajeProcesadoDTO dto = analizarIntencion(mensajeUsuario);

                if (dto.getIntenciones() == null || dto.getIntenciones().isEmpty()) {
                    return "No pude entender tu solicitud. ¿Podrías reformularla?";
                }

                String nombreEspacio = dto.getEspacio();
                String fecha = dto.getFecha();

                StringBuilder respuesta = new StringBuilder();

                for (String intencion : dto.getIntenciones()) {
                    switch (intencion) {
                        case "espacios_disponibles" -> respuesta.append(obtenerEspaciosDisponibles()).append("\n");
                        case "horarios_disponibles" -> respuesta.append(obtenerHorariosDesdeDto(nombreEspacio, fecha)).append("\n");
                        case "costo_reserva" -> respuesta.append(obtenerCostoDesdeBD(nombreEspacio)).append("\n");
                        case "aforo_espacio" -> respuesta.append(obtenerAforoDesdeBD(nombreEspacio)).append("\n");
                        case "estado_mantenimiento" -> respuesta.append(verificarEstadoDesdeBD(nombreEspacio)).append("\n");
                        case "medios_pago" -> respuesta.append(obtenerMediosPago()).append("\n");
                        case "reservas_usuario" -> respuesta.append(obtenerReservasUsuario(usuarioId)).append("\n");
                        case "pagos_usuario" -> respuesta.append(obtenerPagosUsuario(usuarioId)).append("\n");
                        case "detalles_espacio" -> respuesta.append(obtenerDetallesEspacio(nombreEspacio)).append("\n");
                        default -> respuesta.append(llamarAGpt(construirPromptConversacional(mensajeUsuario))).append("\n");
                    }
                }


                return respuesta.toString().trim();

            } catch (Exception e) {
                e.printStackTrace();
                return "Ocurrió un error técnico: " + e.getMessage();
            }
        }

        private String obtenerDetallesEspacio(String nombreEspacio) throws Exception {
            if (nombreEspacio == null || nombreEspacio.isBlank()) {
                return llamarAGpt("Un usuario me pidió detalles de un espacio, pero no especificó cuál. Respóndele amablemente que indique el nombre del espacio que desea consultar.");
            }

            List<EspaciosDeportivos> coincidencias = espaciosDeportivosRepository.findByNombreContaining(nombreEspacio);
            if (coincidencias.isEmpty()) {
                return llamarAGpt("Un usuario está preguntando por un espacio llamado '" + nombreEspacio + "', pero no lo encuentro en la base de datos. Redacta una respuesta amable informando eso.");
            }

            EspaciosDeportivos espacio = coincidencias.get(0);

            // Creamos un prompt que describe el espacio con datos reales
            String prompt = """
        Un usuario me pidió que le dé detalles del siguiente espacio deportivo. Redacta una respuesta natural, amable y clara. Puedes usar emojis si lo deseas, pero no es obligatorio. Redáctalo como si tú fueras el asistente IA.

        Datos del espacio:
        - Nombre: %s
        - Tipo: %s
        - Costo por hora: S/ %s
        - Aforo: %d personas
        - Estado: %s
        - Teléfono: %s
        - Dirección: %s
        """.formatted(
                    espacio.getNombre(),
                    espacio.getTipoEspacio().getNombre(),
                    espacio.getCostoHorario(),
                    espacio.getAforo(),
                    espacio.isOperativo() ? "Operativo" : "En mantenimiento",
                    espacio.getNumContacto(),
                    espacio.getUbicacion()
            );

            return llamarAGpt(prompt);
        }




        private String obtenerReservasUsuario(Integer usuarioId) {
            List<Reservas> reservas = reservasRepository.findByUsuario_IdAndEstadoReserva(
                    usuarioId, Reservas.EstadoReserva.ACTIVA
            );

            if (reservas.isEmpty()) {
                return "No se encontraron reservas activas para este usuario.";
            }

            StringBuilder sb = new StringBuilder("Tus reservas activas:\n");
            for (Reservas r : reservas) {
                sb.append("- Espacio: ").append(r.getEspacioDeportivo().getNombre())
                        .append(" | Fecha: ").append(r.getFechaReserva())
                        .append(" | Hora: ").append(r.getHorario().getHoraInicio())
                        .append(" - ").append(r.getHorario().getHoraFin())
                        .append("\n");
            }

            return sb.toString();
        }

        private String obtenerEspaciosDisponibles() throws Exception {
            List<String> tiposPermitidos = List.of(
                    "Piscina",
                    "Canchas Fútbol",
                    "Cancha de Loza",
                    "Estadios",
                    "Gimnasio",
                    "Pista de Atletismo"
            );


            List<EspaciosDeportivos> espacios = espaciosDeportivosRepository.findAll().stream()
                    .filter(e -> tiposPermitidos.contains(e.getTipoEspacio().getNombre()))
                    .toList();

            if (espacios.isEmpty()) return "Actualmente no hay espacios deportivos disponibles.";

            StringBuilder prompt = new StringBuilder("""
        Un usuario me pidió que le enumere los espacios deportivos disponibles. 
        Redacta una respuesta clara, amigable y natural con los datos a continuación.
        Puedes usar tu estilo propio de redacción como asistente virtual.
        
        Lista de espacios:
        """);

            for (EspaciosDeportivos e : espacios) {
                prompt.append("- ").append(e.getNombre())
                        .append(" (").append(e.getTipoEspacio().getNombre()).append(")\n");
            }

            return llamarAGpt(prompt.toString());
        }






        private String obtenerCostoDesdeBD(String nombreEspacio) {
            if (nombreEspacio == null || nombreEspacio.isBlank()) {
                return "Por favor indica el nombre del espacio para darte el costo.";
            }

            List<EspaciosDeportivos> coincidencias = espaciosDeportivosRepository.findByNombreContaining(nombreEspacio);
            if (coincidencias.isEmpty()) {
                return "No encontré ningún espacio con ese nombre.";
            }

            EspaciosDeportivos espacio = coincidencias.get(0);
            return "El costo por hora de reservar " + espacio.getNombre() + " es de S/" + espacio.getCostoHorario() + ".";
        }



        private String obtenerAforoDesdeBD(String nombreEspacio) {
            if (nombreEspacio == null || nombreEspacio.isBlank()) {
                return "Por favor indícame qué espacio deseas consultar.";
            }

            List<EspaciosDeportivos> coincidencias = espaciosDeportivosRepository.findByNombreContaining(nombreEspacio);
            if (coincidencias.isEmpty()) {
                return "No encontré ningún espacio con ese nombre.";
            }

            EspaciosDeportivos espacio = coincidencias.get(0);
            return "El aforo máximo de " + espacio.getNombre() + " es de " + espacio.getAforo() + " personas.";
        }



        private String verificarEstadoDesdeBD(String nombreEspacio) {
            if (nombreEspacio == null || nombreEspacio.isBlank()) {
                return "Por favor indícame qué espacio deseas consultar.";
            }

            List<EspaciosDeportivos> coincidencias = espaciosDeportivosRepository.findByNombreContaining(nombreEspacio);
            if (coincidencias.isEmpty()) {
                return "No encontré ningún espacio con ese nombre.";
            }

            EspaciosDeportivos espacio = coincidencias.get(0);
            if (espacio.isOperativo()) {
                return "El espacio " + espacio.getNombre() + " está operativo.";
            } else {
                return "El espacio " + espacio.getNombre() + " está actualmente en mantenimiento o no operativo.";
            }
        }



        private String obtenerMediosPago() {
            return "Los medios de pago aceptados son: tarjeta de débito, tarjeta de crédito y billeteras electrónicas.";
        }

        public String obtenerPagosPorUsuario(Integer usuarioId) {
            List<Pagos> pagos = reservasRepository.findPagosByUsuarioId(usuarioId);

            if (pagos.isEmpty()) {
                return "No se encontraron pagos registrados para este usuario.";
            }

            StringBuilder sb = new StringBuilder("Pagos realizados:\n");
            for (Pagos p : pagos) {
                sb.append("- S/").append(p.getCantidad())
                        .append(" el ").append(p.getFechaPago()).append("\n");
            }

            return sb.toString();
        }

        private String obtenerPagosUsuario(Integer usuarioId) {
            List<Pagos> pagos = reservasRepository.findPagosByUsuarioId(usuarioId);

            if (pagos.isEmpty()) {
                return "No se encontraron pagos registrados para este usuario.";
            }

            return "Pagos realizados:\n" + pagos.stream()
                    .map(p -> "- S/" + p.getCantidad() + " el " + p.getFechaPago())
                    .collect(Collectors.joining("\n"));
        }


        private String construirPromptConversacional(String mensaje) {
            String fechaActual = LocalDate.now().toString(); // ej: "2025-06-26"

            return """ 
Eres un asistente virtual inteligente para el sistema de reservas deportivas de la Municipalidad de San Miguel. Tu función principal es ayudar a los ciudadanos (vecinos) a obtener información y gestionar sus reservas en espacios deportivos.

Hoy es %s.

Tu comportamiento debe ser cordial, claro y útil.

TAREAS ESPECÍFICAS QUE PUEDES HACER:
1. Informar sobre espacios deportivos disponibles.
2. Mostrar horarios disponibles para un espacio en una fecha determinada.
3. Indicar el costo por horario (por ahora siempre 1 hora).
4. Mostrar la capacidad máxima (aforo), ubicación y contacto de cada espacio.
5. Informar si un espacio se encuentra en mantenimiento o no operativo.
6. Detallar los medios de pago aceptados.
7. Mostrar las reservas activas del usuario.
8. Mostrar los pagos realizados por el usuario.

CONDICIONES:
- Si el usuario pregunta por un espacio, intenta identificarlo aunque haya errores ortográficos leves.
- El tiempo de reserva es siempre de **1 hora** por defecto.
- Toda la información debe extraerse de una base de datos simulada (o en producción si estás conectado).
- Si el usuario escribe algo que **no está relacionado con reservas deportivas**, actúa como un chatbot general, respondiendo con naturalidad como ChatGPT, pero siempre con el enfoque de que eres un asistente virtual de reservas.

RESPUESTA ESPERADA:
- Cuando detectes intención relacionada al sistema, responde con información concreta.
- Si no entiendes la intención, puedes pedirle al usuario que reformule su pregunta.
- Si la pregunta está fuera de contexto (ej. “¿quién es Messi?”), responde de forma amigable y general como una IA conversacional, pero recordándole que tu enfoque es reservas deportivas.

Usuario: %s
""".formatted(fechaActual, mensaje);
        }


        private String llamarAGpt(String prompt) throws Exception {
            ObjectMapper mapper = new ObjectMapper();

            // Construir el JSON correcto con Jackson
            ObjectNode requestBody = mapper.createObjectNode();
            requestBody.put("model", "gpt-4o");

            ArrayNode messages = mapper.createArrayNode();
            ObjectNode systemMessage = mapper.createObjectNode();
            systemMessage.put("role", "system");
            systemMessage.put("content", "Eres un asistente virtual para reservas deportivas de la Municipalidad de San Miguel.");

            ObjectNode userMessage = mapper.createObjectNode();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);

            messages.add(systemMessage);
            messages.add(userMessage);

            requestBody.set("messages", messages);
            requestBody.put("temperature", 0.5);

            // Convertir a JSON String
            String json = mapper.writeValueAsString(requestBody);

            // Enviar solicitud
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode responseJson = mapper.readTree(response.body());

            // Validación robusta
            if (responseJson.has("error")) {
                String errorMessage = responseJson.get("error").get("message").asText();
                throw new RuntimeException("Error de OpenAI: " + errorMessage);
            }

            if (responseJson.has("choices") && responseJson.get("choices").isArray()
                    && responseJson.get("choices").size() > 0) {
                return responseJson.get("choices").get(0).get("message").get("content").asText();
            } else {
                throw new RuntimeException("Respuesta inesperada de OpenAI: " + response.body());
            }
        }
        public MensajeProcesadoDTO analizarIntencion(String mensajeUsuario) throws Exception {
            String prompt = """
            Eres un asistente virtual inteligente para el sistema de reservas deportivas de la Municipalidad de San Miguel. Tu función principal es ayudar a los ciudadanos (vecinos) a obtener información y gestionar sus reservas en espacios deportivos.
    
            Tu tarea en este momento es analizar el siguiente mensaje del usuario y responder con un JSON que contenga:
    
            {
             "intenciones": ["costo_reserva", "horarios_disponibles"],
             "espacio": "piscina Diego Ferre",
             "fecha": "2025-07-15"
            }
    // En el string 'prompt' dentro del método analizarIntencion
    - "Dame los detalles de la piscina" ⇒ intencion: detalles_espacio, espacio: piscina, fecha: null
    - "Qué características tiene el gimnasio?" ⇒ intencion: detalles_espacio, espacio: gimnasio, fecha: null
    - "Enumérame los espacios deportivos" ⇒ intencion: espacios_disponibles, espacio: null, fecha: null
    - "Dame la lista numerada de espacios" ⇒ intencion: espacios_disponibles, espacio: null, fecha: null
    
            Considera lo siguiente:
            - Si el usuario pregunta por un espacio, intenta identificarlo aunque tenga errores ortográficos leves.
            - Interpreta expresiones como "mañana", "hoy", "viernes", "el 10 de julio" y conviértelas a formato YYYY-MM-DD.
            - Si no hay suficiente información para un campo, devuélvelo como null.
            - Si el mensaje no está relacionado a reservas deportivas, clasifica la intención como "otro".
    
            Ejemplos:
            - "¿Qué horarios hay el viernes en la piscina?" ⇒ intencion: horarios_disponibles, espacio: piscina, fecha: 2025-06-28
            - "¿Qué aforo tiene el gimnasio?" ⇒ intencion: aforo_espacio, espacio: gimnasio, fecha: null
            - "¿Puedo pagar con Yape?" ⇒ intencion: medios_pago, espacio: null, fecha: null
            - "Hola, ¿cómo estás?" ⇒ intencion: otro, espacio: null, fecha: null
    
            Mensaje del usuario: "%s"
            Responde solo el JSON, sin explicaciones adicionales.
            """.formatted(mensajeUsuario);

            String json = llamarAGpt(prompt);
// Extraer solo el bloque JSON usando regex
            Pattern pattern = Pattern.compile("\\{.*?\\}", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(json);
            if (matcher.find()) {
                String jsonValido = matcher.group();
                return mapper.readValue(jsonValido, MensajeProcesadoDTO.class);
            } else {
                throw new RuntimeException("No se encontró un JSON válido en la respuesta: " + json);
            }
        }


    }
