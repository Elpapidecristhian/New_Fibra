package com.example.gtics_ta.DTO;

import com.example.gtics_ta.Entity.EspaciosDeportivos;
import com.example.gtics_ta.Entity.HorarioReservado;
import com.example.gtics_ta.Entity.Pagos;
import com.example.gtics_ta.Entity.Usuario;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ReservasDTO {
    private Usuario usuario;
    private EspaciosDeportivos espaciosDeportivos;
    private Pagos Pago;
    private HorarioReservado HorarioReservado;
    private Date fechaReserva;
}
