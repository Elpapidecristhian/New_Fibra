
package com.example.gtics_ta.repository;


import com.example.gtics_ta.Entity.EspaciosDeportivos;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;

public interface EspaciosDeportivosRepository extends JpaAttributeConverter<EspaciosDeportivos, Integer> {
    long countByOperativo(boolean operativo); // true = disponible, false = ocupado

}

