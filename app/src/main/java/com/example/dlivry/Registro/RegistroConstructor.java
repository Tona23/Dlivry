package com.example.dlivry.Registro;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class RegistroConstructor implements Serializable {

    @Exclude
    private String key;
    private String nombre;
    private String apellido;
    private String fecha;
    private String numero;
    private String correo;
    private String contrasena;
    private String ruta;
    private String confirmado;

    public RegistroConstructor(){}
    public RegistroConstructor(String nombre, String apellido, String fecha, String numero, String correo, String contrasena, String ruta, String confirmado) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.fecha = fecha;
        this.numero = numero;
        this.correo = correo;
        this.contrasena = contrasena;
        this.ruta = ruta;
        this.confirmado = confirmado;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getConfirmado() {
        return confirmado;
    }

    public void setConfirmado(String confirmado) {
        this.confirmado = confirmado;
    }
}