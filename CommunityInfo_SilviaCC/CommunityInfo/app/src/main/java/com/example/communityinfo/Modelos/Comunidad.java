package com.example.communityinfo.Modelos;

public class Comunidad {
    private String cif;
    private String nombreComunidad;
    private String direccion;

    // CONSTRUCTORES
    public Comunidad() { /* Construcctor vacio para Firebase */ }
    public Comunidad(String cif, String nombreComunidad, String direccion){
        this.cif = cif;
        this.nombreComunidad = nombreComunidad;
        this.direccion = direccion;
    }

    // GETTERS Y SETTERS
    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getNombreComunidad() {
        return nombreComunidad;
    }

    public void setNombreComunidad(String nombreComunidad) {
        this.nombreComunidad = nombreComunidad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
