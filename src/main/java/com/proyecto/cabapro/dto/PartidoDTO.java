// NUEVO 
package com.proyecto.cabapro.dto;

import com.proyecto.cabapro.enums.EstadoPartido;
import java.time.LocalDateTime;

public class PartidoDTO {
    private int idPartido;
    private LocalDateTime fecha;
    private String lugar;
    private EstadoPartido estadoPartido;
    private String equipoLocal;
    private String equipoVisitante;


    private TorneoDTO torneo;

    public TorneoDTO getTorneo() { return torneo; }
    public void setTorneo(TorneoDTO torneo) { this.torneo = torneo; }


    // Getters y setters
    public int getIdPartido() { return idPartido; }
    public void setIdPartido(int idPartido) { this.idPartido = idPartido; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }

    public EstadoPartido getEstadoPartido() { return estadoPartido; }
    public void setEstadoPartido(EstadoPartido estadoPartido) { this.estadoPartido = estadoPartido; }

    public String getEquipoLocal() { return equipoLocal; }
    public void setEquipoLocal(String equipoLocal) { this.equipoLocal = equipoLocal; }

    public String getEquipoVisitante() { return equipoVisitante; }
    public void setEquipoVisitante(String equipoVisitante) { this.equipoVisitante = equipoVisitante; }
}
