package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.HorarioReservado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HorarioReservadoRepository extends JpaRepository<HorarioReservado, Long> {
}
