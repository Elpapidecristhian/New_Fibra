package com.example.gtics_ta.entity;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Usuario")
    private Integer idUsuario;

    @Column(name = "nombres", nullable = false, length = 45)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 45)
    private String apellidos;

    @Column(name = "correo", nullable = false, length = 45)
    private String correo;

    @Column(name = "contrasenia", nullable = false, length = 45)
    private String contrasenia;

    @Column(name = "direccion", length = 90)
    private String direccion;

    @Column(name = "dni", nullable = false)
    private Integer dni;

    @Column(name = "numCelular")
    private Integer numCelular;

    @ManyToOne
    @JoinColumn(name = "idRol", nullable = false)
    private Roles rol;

    @Column(name = "fotoPerfil")
    private byte[] fotoPerfil;

    @Column(name = "isBaneado", nullable = false)
    private Boolean isBaneado;

    @Column(name = "fechaNacimiento")
    private java.sql.Date fechaNacimiento;

    // Getters y Setters

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
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

    public Integer getDni() {
        return dni;
    }

    public void setDni(Integer dni) {
        this.dni = dni;
    }

    public Integer getNumCelular() {
        return numCelular;
    }

    public void setNumCelular(Integer numCelular) {
        this.numCelular = numCelular;
    }

    public Roles getRol() {
        return rol;
    }

    public void setRol(Roles rol) {
        this.rol = rol;
    }

    public byte[] getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(byte[] fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public Boolean getBaneado() {
        return isBaneado;
    }

    public void setBaneado(Boolean baneado) {
        isBaneado = baneado;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}
