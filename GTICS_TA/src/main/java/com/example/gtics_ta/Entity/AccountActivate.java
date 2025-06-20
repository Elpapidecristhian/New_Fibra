package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "accountactivate")
public class AccountActivate {
    @Id
    private String token;
    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}
