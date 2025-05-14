package com.example.gtics_ta.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "mediospago")
public class MedioPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idMediosPago;
    private String medioPago;
    public int getIdMediosPago() {
        return idMediosPago;
    }

    public void setIdMediosPago(int idMediosPago) {
        this.idMediosPago = idMediosPago;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }


}
