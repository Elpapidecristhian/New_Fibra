package com.example.gtics_ta.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idUsuario")
    private int idUsuario;
    private String nombres;
    private String apellidos;
    private String correo;
    private String contrasenia;
    private String direccion;
    private int dni;
    private int numCelular;
    @ManyToOne
    @JoinColumn(name = "idRol",nullable = false)
    private Rol idRol;
    @Lob
    private byte[] fotoPerfil;
    @Column(name = "isBaneado", columnDefinition = "TINYINT(1)")  // Definir como TINYINT en la base de datos
    private boolean isBaneado = false;
    private LocalDate fechaNacimiento;

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public int getNumCelular() {
        return numCelular;
    }

    public void setNumCelular(int numCelular) {
        this.numCelular = numCelular;
    }

    public Rol getIdRol() {
        return idRol;
    }

    public void setIdRol(Rol idRol) {
        this.idRol = idRol;
    }

    public byte[] getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(byte[] fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public boolean isBaneado() {
        return isBaneado;
    }

    public void setBaneado(boolean baneado) {
        isBaneado = baneado;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }



}
