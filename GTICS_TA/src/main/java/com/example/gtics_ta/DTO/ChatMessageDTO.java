package com.example.gtics_ta.DTO;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class ChatMessageDTO {
    private String mensajeUsuario;
    private String respuestaBot;
    private Integer usuarioId;
    private String espacioDetectado;
    private LocalDate fechaDetectada;
    private List<String> intenciones;   // Intenciones detectadas, opcional

}