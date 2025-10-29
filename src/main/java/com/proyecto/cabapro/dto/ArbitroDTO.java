// NUEVO 

package com.proyecto.cabapro.dto;

import java.util.List;

public class ArbitroDTO {

    private int id;
    private String nombre;
    private String apellido;
    private String correo;
    private String rol;
    private String especialidad;
    private String escalafon;
    private String urlFoto;


    private List<AsignacionDTO> asignaciones;

    public List<AsignacionDTO> getAsignaciones() {
        return asignaciones;
    }
    public void setAsignaciones(List<AsignacionDTO> asignaciones) {
        this.asignaciones = asignaciones;
    }
    private List<PartidoDTO> partidos;

    public List<PartidoDTO> getPartidos() { return partidos; }
    public void setPartidos(List<PartidoDTO> partidos) { this.partidos = partidos; }

    // ===== Getters y Setters =====

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getEscalafon() { return escalafon; }
    public void setEscalafon(String escalafon) { this.escalafon = escalafon; }

    public String getUrlFoto() { return urlFoto; }
    public void setUrlFoto(String urlFoto) { this.urlFoto = urlFoto; }
}
