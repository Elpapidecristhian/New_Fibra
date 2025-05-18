package com.example.gtics_ta.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HorarioReservadoDTO {
    private Integer idEspacio;    // ID del espacio deportivo
    private Integer idHorario;    // ID del horario específico (hora inicio-fin)
    private String fecha;         // YYYY-MM-DD
    private String horaInicio;
    private String horaFin;
    private String estado;        // "Reservado" o "Mantenimiento"
    private String motivo;        // Opcional, para mantenimiento o notas
}
