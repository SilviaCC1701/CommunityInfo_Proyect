package com.example.communityinfo.Modelos;

public class Comunicado {
    private long fecha;
    private String titulo;
    private String asunto;
    private String contenido;

    // CONSTRUCTORES
    public Comunicado(){ /* Construcctor vacio para Firebase */ }
    public Comunicado(long fecha, String titulo, String asunto, String contenido){
        this.fecha = fecha;
        this.titulo = titulo;
        this.asunto = asunto;
        this.contenido = contenido;
    }

    // GETTERS Y SETTERS
    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
