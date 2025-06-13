package com.example.gtics_ta.Services;

import org.springframework.stereotype.Service;

@Service
public class ChatbotService {

    public String generarRespuesta(String mensajeUsuario) {
        mensajeUsuario = mensajeUsuario.toLowerCase();

        if (mensajeUsuario.contains("horario") || mensajeUsuario.contains("disponible")) {
            return "Puedes revisar los horarios disponibles haciendo clic en 'Reservar' en el espacio que te interesa.";
        } else if (mensajeUsuario.contains("costo") || mensajeUsuario.contains("precio")) {
            return "El costo por hora está indicado debajo de cada espacio. ¿Quieres saber el de alguno en particular?";
        } else if (mensajeUsuario.contains("cancelar") || mensajeUsuario.contains("devolución")) {
            return "Puedes cancelar desde tu panel de reservas. Las devoluciones dependen del tiempo restante antes del horario reservado.";
        } else {
            return "¡Estoy aquí para ayudarte! Puedes preguntarme sobre horarios, precios, cancelaciones o mantenimiento.";
        }
    }
}
