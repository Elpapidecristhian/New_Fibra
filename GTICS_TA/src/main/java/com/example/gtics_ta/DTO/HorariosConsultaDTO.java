package com.example.gtics_ta.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public interface HorariosConsultaDTO {
    Integer getIdHorarios();
    Date getHoraInicio();
    Date getHoraFin();
    Integer getIdEspacio();
    Integer getReservado();

}
