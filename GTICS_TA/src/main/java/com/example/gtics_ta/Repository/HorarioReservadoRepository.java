package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.HorarioReservado;
import com.example.gtics_ta.Entity.Horarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface HorarioReservadoRepository extends JpaRepository<HorarioReservado, Long> {

    HorarioReservado findByHorario_IdAndFecha(Integer id, LocalDate fecha);

    void deleteAllByHorario(Horarios h);
}
