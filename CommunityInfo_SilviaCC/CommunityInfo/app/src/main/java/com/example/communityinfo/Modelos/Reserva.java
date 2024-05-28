package com.example.communityinfo.Modelos;

public class Reserva {
    private String id;
    private long fechaReserva;
    private String areaUbicacion;
    private String horaInicio;
    private String horaFin;
    private String motivo;
    private String usuarioId;
    // CONSTRUCTORES
    public Reserva(){ /* Construcctor vacio para Firebase */ }
    public Reserva(String id, long fechaReserva, String areaUbicacion, String horaInicio, String horaFin, String motivo, String usuarioId) {
        this.id = id;
        this.fechaReserva = fechaReserva;
        this.areaUbicacion = areaUbicacion;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.motivo = motivo;
        this.usuarioId = usuarioId;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(long fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getAreaUbicacion() {
        return areaUbicacion;
    }

    public void setAreaUbicacion(String areaUbicacion) {
        this.areaUbicacion = areaUbicacion;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }
}
