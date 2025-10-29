// NUEVO 

package com.proyecto.cabapro.dto;

import com.proyecto.cabapro.enums.CategoriaTorneo;
import com.proyecto.cabapro.enums.TipoTorneo;
import java.time.LocalDateTime;

public class TorneoDTO {
    private int idTorneo;
    private String nombre;
    private TipoTorneo tipoTorneo;
    private CategoriaTorneo categoria;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    // Getters y setters
    public int getIdTorneo() { return idTorneo; }
    public void setIdTorneo(int idTorneo) { this.idTorneo = idTorneo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public TipoTorneo getTipoTorneo() { return tipoTorneo; }
    public void setTipoTorneo(TipoTorneo tipoTorneo) { this.tipoTorneo = tipoTorneo; }

    public CategoriaTorneo getCategoria() { return categoria; }
    public void setCategoria(CategoriaTorneo categoria) { this.categoria = categoria; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
}

