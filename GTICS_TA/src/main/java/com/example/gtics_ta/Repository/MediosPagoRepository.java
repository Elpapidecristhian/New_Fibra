package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.MediosPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediosPagoRepository extends JpaRepository<MediosPago, Integer> {
}
