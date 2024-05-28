package com.example.communityinfo.Modelos;

public class Reserva {
    private long fechaHora;
    private String areaUbicacion;
    private long horaInicio;
    private long horaFin;
    private String motivo;
    // CONSTRUCTORES
    public Reserva(){ /* Construcctor vacio para Firebase */ }
    public Reserva(long fechaHora, String areaUbicacion, long horaInicio, long horaFin, String motivo){
        this.fechaHora = fechaHora;
        this.areaUbicacion = areaUbicacion;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.motivo = motivo;
    }

    // GETTERS Y SETTERS
    public long getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(long fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getAreaUbicacion() {
        return areaUbicacion;
    }

    public void setAreaUbicacion(String areaUbicacion) {
        this.areaUbicacion = areaUbicacion;
    }

    public long getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(long horaInicio) {
        this.horaInicio = horaInicio;
    }

    public long getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(long horaFin) {
        this.horaFin = horaFin;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
