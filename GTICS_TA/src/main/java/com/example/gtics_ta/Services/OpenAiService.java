    package com.example.gtics_ta.Services;

    import com.example.gtics_ta.DTO.ChatMessageDTO;
    import com.example.gtics_ta.DTO.HorariosConsultaDTO;
    import com.example.gtics_ta.DTO.MensajeProcesadoDTO;
    import com.example.gtics_ta.Entity.*;
    import com.example.gtics_ta.Repository.*;
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
    import java.util.ArrayList;
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
        @Autowired private PiscinasRepository piscinasRepository;
        @Autowired private GimnasiosRepository gimnasiosRepository;
        @Autowired private CanchasFutbolRepository canchasFutbolRepository;
        @Autowired private EstadiosRepository estadiosRepository;
        @Autowired private PistasAtletismoRepository pistasAtletismoRepository;

        public String obtenerHorariosDesdeDto(String nombreEspacio, String fechaStr) {
            if (nombreEspacio == null || fechaStr == null) {
                return "Por favor indícame el nombre del espacio y la fecha para mostrar los horarios disponibles.";
            }

            List<EspaciosDeportivos> espacios = espaciosDeportivosRepository.findByNombreContaining(nombreEspacio);
            if (espacios.isEmpty()) {
                return "No se encontró ningún espacio con ese nombre.";
            }

            EspaciosDeportivos espacio = espacios.get(0);
            LocalDate fecha;
            try {
                fecha = LocalDate.parse(fechaStr);
            } catch (Exception e) {
                return "La fecha que proporcionaste no tiene un formato válido. Usa el formato AAAA-MM-DD.";
            }

            List<HorariosConsultaDTO> listaHorarios = horariosRepository.obtenerHorariosConsulta(fecha, espacio.getId());
            List<HorariosConsultaDTO> disponibles = listaHorarios.stream()
                    .filter(h -> h.getReservado() == null || h.getReservado() == 0)
                    .toList();

            try {
                if (disponibles.isEmpty()) {
                    String promptSinHorarios = """
                Un usuario preguntó por los horarios disponibles en "%s" para la fecha %s, pero no hay ninguno disponible.
                
                Redacta una respuesta empática y amable informándole que no hay disponibilidad ese día. Puedes sugerirle intentar con otra fecha.
                
                Puedes usar emojis si deseas.
                """.formatted(espacio.getNombre(), fechaStr);

                    return llamarAGpt(promptSinHorarios);
                }

                StringBuilder raw = new StringBuilder("""
                Quiero que redactes una respuesta amable, clara y con estilo natural para un usuario que quiere saber los horarios disponibles en un espacio deportivo.
                
                Estos son los datos:
                - Nombre del espacio: %s
                - Fecha: %s
                
                Lista de horarios disponibles:
                """.formatted(espacio.getNombre(), fechaStr));

                for (HorariosConsultaDTO h : disponibles) {
                    raw.append("- ").append(h.getHoraInicio()).append(" - ").append(h.getHoraFin()).append("\n");
                }

                String prompt = raw.toString() + "\nPuedes usar emojis si deseas.";

                return llamarAGpt(prompt);

            } catch (Exception e) {
                return "Ocurrió un error al procesar los horarios. Inténtalo más tarde.";
            }
        }

        private static final List<String> TIPOS_VALIDOS = List.of(
                "Piscina", "Gimnasio", "Estadio", "Cancha de Fútbol Grass", "Cancha de Loza", "Pista de Atletismo"
        );

        public String generarRespuesta(ChatMessageDTO mensajeDTO) {
            String mensajeUsuario = mensajeDTO.getMensajeUsuario();
            Integer usuarioId = mensajeDTO.getUsuarioId();

            try {
                MensajeProcesadoDTO dto = analizarIntencion(mensajeUsuario);
                if (mensajeDTO.getEspacioDetectado() != null && (dto.getEspacio() == null || dto.getEspacio().isBlank())) {
                    dto.setEspacio(mensajeDTO.getEspacioDetectado());
                }

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
            String tipo = espacio.getTipoEspacio().getNombre().toLowerCase();
            StringBuilder detallesExtra = new StringBuilder();

            switch (tipo) {
                case "piscina" -> {
                    Piscinas piscina = piscinasRepository.findById(espacio.getId()).orElse(null);
                    if (piscina != null) {
                        detallesExtra.append("- Tipo de piscina: ").append(piscina.getTipoPiscina()).append("\n")
                                .append("- Profundidad: de ").append(piscina.getProfundidadMin()).append(" m a ")
                                .append(piscina.getProfundidadMax()).append(" m\n")
                                .append("- ¿Climatizada?: ").append(piscina.isClimatizada() ? "Sí" : "No").append("\n")
                                .append("- Número máximo de carriles: ").append(piscina.getNumCarrilMax()).append("\n")
                                .append("- Requisitos: ").append(piscina.getRequisitos()).append("\n");
                    }
                }
                case "gimnasio" -> {
                    Gimnasios gimnasio = gimnasiosRepository.findById(espacio.getId()).orElse(null);
                    if (gimnasio != null) {
                        detallesExtra.append("- Cantidad de máquinas: ").append(gimnasio.getCantidadMaquinas()).append("\n")
                                .append("- Tipos de máquinas: ").append(gimnasio.getTiposMaquinas()).append("\n")
                                .append("- ¿Tiene sauna?: ").append(Boolean.TRUE.equals(gimnasio.getTieneSauna()) ? "Sí" : "No").append("\n")
                                .append("- ¿Tiene duchas?: ").append(Boolean.TRUE.equals(gimnasio.getTieneDuchas()) ? "Sí" : "No").append("\n")
                                .append("- Costo semanal: S/").append(gimnasio.getCostoSemanal()).append("\n")
                                .append("- Costo mensual: S/").append(gimnasio.getCostoMensual()).append("\n")
                                .append("- Costo anual: S/").append(gimnasio.getCostoAnual()).append("\n");
                    }
                }
                case "cancha de fútbol grass", "cancha de fútbol", "cancha de loza" -> {
                    CanchasFutbol cancha = canchasFutbolRepository.findById(espacio.getId()).orElse(null);
                    if (cancha != null) {
                        detallesExtra.append("- Tipo de superficie: ").append(cancha.getTipoSuperficie()).append("\n")
                                .append("- ¿Iluminación nocturna?: ").append(cancha.isIluminacionNocturna() ? "Sí" : "No").append("\n")
                                .append("- ¿Balones disponibles?: ").append(cancha.isBalonesDisponibles() ? "Sí" : "No").append("\n")
                                .append("- Tamaño de cancha: ").append(cancha.getAncho()).append(" m x ").append(cancha.getAlto()).append(" m\n");
                    }
                }
                case "estadio" -> {
                    Estadios estadio = estadiosRepository.findById(espacio.getId()).orElse(null);
                    if (estadio != null) {
                        detallesExtra.append("- Aforo: ").append(estadio.getAforo()).append(" personas\n")
                                .append("- Uso permitido: ").append(estadio.getUsoPermitido()).append("\n")
                                .append("- ¿Seguridad disponible?: ").append(estadio.isSeguridadDisponible() ? "Sí" : "No").append("\n")
                                .append("- ¿Sonido/Pantallas?: ").append(estadio.isSonidoPantallasDisponible() ? "Sí" : "No").append("\n")
                                .append("- ¿Iluminación profesional?: ").append(estadio.isIluminacionProfesionalDisponible() ? "Sí" : "No").append("\n");
                    }
                }
                case "pista de atletismo" -> {
                    PistasAtletismo pista = pistasAtletismoRepository.findById(espacio.getId()).orElse(null);
                    if (pista != null) {
                        detallesExtra.append("- Tipo de superficie: ").append(pista.getTipoSuperficie()).append("\n")
                                .append("- Longitud de la pista: ").append(pista.getLongitud()).append(" metros\n")
                                .append("- Implementos disponibles: ").append(pista.getImplementos()).append("\n");
                    }
                }

            }

            String prompt = """
    Un usuario me pidió que le dé detalles del siguiente espacio deportivo. Redacta una respuesta natural, amable y clara. Puedes usar emojis si lo deseas, pero no es obligatorio. Redáctalo como si tú fueras el asistente IA.

    Datos generales:
    - Nombre: %s
    - Tipo: %s
    - Costo por hora: S/ %s
    - Aforo: %d personas
    - Estado: %s
    - Teléfono: %s
    - Dirección: %s

    Detalles adicionales:
    %s
    """.formatted(
                    espacio.getNombre(),
                    espacio.getTipoEspacio().getNombre(),
                    espacio.getCostoHorario(),
                    espacio.getAforo(),
                    espacio.isOperativo() ? "Operativo" : "En mantenimiento",
                    espacio.getNumContacto(),
                    espacio.getUbicacion(),
                    detallesExtra.toString()
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






        private String obtenerCostoDesdeBD(String nombreEspacio) throws Exception {
            if (nombreEspacio == null || nombreEspacio.isBlank()) {
                return llamarAGpt("Un usuario me pidió el costo por reservar un espacio, pero no indicó el nombre. Respóndele amablemente que lo indique.");
            }

            List<EspaciosDeportivos> coincidencias = espaciosDeportivosRepository.findByNombreContaining(nombreEspacio);
            if (coincidencias.isEmpty()) {
                return llamarAGpt("Un usuario preguntó por el precio de un espacio llamado '" + nombreEspacio + "', pero no existe en la base de datos. Informa amablemente.");
            }

            EspaciosDeportivos espacio = coincidencias.get(0);
            String prompt = """
    Un usuario quiere saber el costo de reservar el siguiente espacio deportivo:

    - Nombre: %s
    - Tipo: %s
    - Costo por hora: S/ %s

    Redacta una respuesta clara, amigable y natural, como si fueras un asistente virtual, porn emojis si deseas.
    """.formatted(
                    espacio.getNombre(),
                    espacio.getTipoEspacio().getNombre(),
                    espacio.getCostoHorario()
            );

            return llamarAGpt(prompt);
        }




        private String obtenerAforoDesdeBD(String nombreEspacio) throws Exception {
            if (nombreEspacio == null || nombreEspacio.isBlank()) {
                return llamarAGpt("Un usuario me pidió el aforo de un espacio, pero no indicó el nombre. Respóndele amablemente que lo indique.");
            }

            List<EspaciosDeportivos> coincidencias = espaciosDeportivosRepository.findByNombreContaining(nombreEspacio);
            if (coincidencias.isEmpty()) {
                return llamarAGpt("Un usuario preguntó por el aforo de un espacio llamado '" + nombreEspacio + "', pero no lo encuentro en la base de datos. Informa amablemente.");
            }

            EspaciosDeportivos espacio = coincidencias.get(0);
            String prompt = """
    Un usuario desea saber el aforo máximo del siguiente espacio deportivo:

    - Nombre: %s
    - Tipo: %s
    - Aforo: %d personas

    Redacta una respuesta clara, amable y natural, como si fueras un asistente virtual especializado, pon emojis si deseas.
    """.formatted(
                    espacio.getNombre(),
                    espacio.getTipoEspacio().getNombre(),
                    espacio.getAforo()
            );

            return llamarAGpt(prompt);
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
            systemMessage.put("content", "Eres un asistente virtual especializado en reservas deportivas para la Municipalidad de San Miguel. Tu objetivo es ayudar a los usuarios respondiendo sus preguntas de forma clara, amable y útil. Interpreta la intención detrás de cada consulta y responde de la manera más adecuada posible, ya sea de forma directa o haciendo preguntas adicionales si es necesario.");

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

        Tu tarea ahora es analizar el siguiente mensaje del usuario y responder con un JSON que contenga:

        {
         "intenciones": [...],      // Ej: ["costo_reserva", "horarios_disponibles"]
         "espacio": "...",          // Ej: "piscina Diego Ferre"
         "fecha": "..."             // Ej: "2025-07-15"
          "detalle": "..."  // ← NUEVO
                   
        }
NUEVO CAMPO: "detalle"
- Si el usuario pregunta por un atributo o característica específica del espacio (como "requisitos", "aforo", "costo", "duchas", "sauna", etc.), indícalo en el campo "detalle".
- Si el usuario no pidió nada específico, pon "detalle": null.
- Este campo se usa solo con la intención "detalles_espacio".

        REQUISITOS:
        - Si el usuario pregunta por un espacio, intenta identificarlo aunque tenga errores ortográficos leves.
        - Interpreta expresiones como "mañana", "hoy", "viernes", "el 10 de julio" y conviértelas a formato YYYY-MM-DD.
        - Si se menciona una fecha anterior a hoy, ignórala y deja "fecha": null.
        - Si no hay suficiente información para un campo, devuélvelo como null.
        - Si el mensaje no está relacionado con reservas deportivas, clasifica la intención como "otro".

        INTENCIONES POSIBLES:
        - espacios_disponibles
        - horarios_disponibles
        - costo_reserva
        - aforo_espacio
        - ubicacion_espacio
        - contacto_espacio
        - estado_operativo
        - detalles_espacio
        - medios_pago
        - reservas_usuario
        - pagos_usuario
        - devoluciones
        - otro

        EJEMPLOS:
- "¿Cuáles son los requisitos de la piscina?" ⇒ intencion: detalles_espacio, espacio: piscina, detalle: "requisitos"
- "¿Tiene duchas el gimnasio?" ⇒ intencion: detalles_espacio, espacio: gimnasio, detalle: "duchas"
- "Dame los detalles de la piscina" ⇒ intencion: detalles_espacio, espacio: piscina, detalle: null
        - "¿Qué horarios hay el viernes en la piscina?" ⇒ intencion: horarios_disponibles, espacio: piscina, fecha: YYYY-MM-DD
        - "¿Cuánto cuesta reservar el gimnasio?" ⇒ intencion: costo_reserva, espacio: gimnasio, fecha: null
        - "¿Qué aforo tiene el gimnasio?" ⇒ intencion: aforo_espacio, espacio: gimnasio, fecha: null
        - "¿Puedo pagar con Yape?" ⇒ intencion: medios_pago, espacio: null, fecha: null
        - "Hola, ¿cómo estás?" ⇒ intencion: otro, espacio: null, fecha: null

        IMPORTANTE:
- Si el usuario pregunta por un detalle específico (por ejemplo: requisitos, duchas, máquinas, sauna, aforo, tipo de superficie, iluminación, etc.), añade en el JSON un campo extra: "detalle": "requisitos", "duchas", etc.
        - Puedes detectar más de una intención si el usuario pide múltiples cosas.

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
        public ChatMessageDTO procesarMensaje(String mensaje) {
            ChatMessageDTO dto = new ChatMessageDTO();
            dto.setMensajeUsuario(mensaje);

            // Aquí agregas lógica para detectar espacio deportivo y fecha
            // Ejemplo simple, usa regex o keywords:
            String espacio = detectarEspacio(mensaje);
            LocalDate fecha = detectarFecha(mensaje);

            dto.setEspacioDetectado(espacio);
            dto.setFechaDetectada(fecha);

            // También detectar intenciones si quieres
            List<String> intenciones = detectarIntenciones(mensaje);
            dto.setIntenciones(intenciones);

            return dto;
        }

        public String detectarEspacio(String mensaje) {
            // Aquí pones la lógica para detectar el espacio deportivo mencionado en el mensaje
            // Ejemplo simple:
            List<String> espacios = List.of(
                    "Piscina",
                    "Canchas Fútbol",
                    "Cancha de Loza",
                    "Estadios",
                    "Gimnasio",
                    "Pista de Atletismo"
            );

            for (String espacio : espacios) {
                if (mensaje.toLowerCase().contains(espacio.toLowerCase())) {
                    return espacio;
                }
            }
            return null;
        }
        public LocalDate detectarFecha(String mensaje) {
            if (mensaje.toLowerCase().contains("mañana")) {
                return LocalDate.now().plusDays(1);
            }
            if (mensaje.toLowerCase().contains("hoy")) {
                return LocalDate.now();
            }
            // Puedes agregar lógica más avanzada con regex o NLP aquí
            return null;
        }
        public List<String> detectarIntenciones(String mensaje) {
            List<String> intenciones = new ArrayList<>();
            String msgLower = mensaje.toLowerCase();

            if (msgLower.contains("precio") || msgLower.contains("costo") || msgLower.contains("cuesta")) {
                intenciones.add("consultar_precio");
            }
            if (msgLower.contains("aforo") || msgLower.contains("capacidad")) {
                intenciones.add("consultar_aforo");
            }
            if (msgLower.contains("horario") || msgLower.contains("disponible")) {
                intenciones.add("consultar_horarios");
            }
            if (msgLower.contains("reservar") || msgLower.contains("hacer reserva")) {
                intenciones.add("hacer_reserva");
            }
            if (intenciones.isEmpty()) {
                intenciones.add("general");
            }
            return intenciones;
        }

    }
