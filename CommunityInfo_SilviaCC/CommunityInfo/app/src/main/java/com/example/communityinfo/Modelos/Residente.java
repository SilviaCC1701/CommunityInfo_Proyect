package com.example.communityinfo.Modelos;

public class Residente {
    private String dni;
    private String nombre;
    private String nombreUsuario;
    private String telefono;
    private String direccion;
    private String email;
    private String uidUser;

    // CONSTRUCTORES
    public Residente(){ /* Construcctor vacio para Firebase */ }
    public Residente(String dni, String nombre, String nombreUsuario, String telefono, String direccion, String email, String uidUser){
        this.dni = dni;
        this.nombre = nombre;
        this.nombreUsuario = nombreUsuario;
        this.telefono = telefono;
        this.direccion = direccion;
        this.email = email;
        this.uidUser = uidUser;
    }

    // GETTERS Y SETTERS

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
    }
}
