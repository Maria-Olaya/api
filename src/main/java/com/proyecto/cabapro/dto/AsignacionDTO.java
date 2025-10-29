// NUEVO 

package com.proyecto.cabapro.dto;

import com.proyecto.cabapro.enums.EstadoAsignacion;
import java.math.BigDecimal;
import java.time.LocalDate;

public class AsignacionDTO {
    private Long id;
    private LocalDate fechaAsignacion;
    private EstadoAsignacion estado;
    private BigDecimal monto;
    private PartidoDTO partido;
    private TorneoDTO torneo;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDate fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public EstadoAsignacion getEstado() { return estado; }
    public void setEstado(EstadoAsignacion estado) { this.estado = estado; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public PartidoDTO getPartido() { return partido; }
    public void setPartido(PartidoDTO partido) { this.partido = partido; }

    public TorneoDTO getTorneo() { return torneo; }
    public void setTorneo(TorneoDTO torneo) { this.torneo = torneo; }
}
