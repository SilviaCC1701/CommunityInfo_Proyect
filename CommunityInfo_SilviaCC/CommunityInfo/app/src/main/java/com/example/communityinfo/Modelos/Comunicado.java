package com.example.communityinfo.Modelos;

public class Comunicado {
    private String id;
    private long fecha; // es una fecha en Epoch timestamp
    private String titulo;
    private String asunto;
    private String contenido;

    // CONSTRUCTORES
    public Comunicado(){ /* Construcctor vacio para Firebase */ }
    public Comunicado(String id, long fecha, String titulo, String asunto, String contenido){
        this.id = id;
        this.fecha = fecha;
        this.titulo = titulo;
        this.asunto = asunto;
        this.contenido = contenido;
    }

    // GETTERS Y SETTERS

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
