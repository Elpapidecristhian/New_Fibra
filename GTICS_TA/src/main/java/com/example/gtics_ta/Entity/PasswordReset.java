package com.example.gtics_ta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "passwdreset")
public class PasswordReset {
    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    @Id
    private String token;
    private LocalDateTime expiracion;
}
