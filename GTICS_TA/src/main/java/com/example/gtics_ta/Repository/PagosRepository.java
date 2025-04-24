package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.Pagos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagosRepository extends JpaRepository<Pagos,Integer> {

}
