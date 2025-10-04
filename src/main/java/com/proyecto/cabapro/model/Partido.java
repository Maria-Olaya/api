package com.proyecto.cabapro.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.proyecto.cabapro.enums.EstadoPartido;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "partidos")
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPartido;

    private LocalDateTime fecha;
    private String lugar;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_partido")
    private EstadoPartido estadoPartido = EstadoPartido.PROGRAMADO; // valor por defecto

    private String equipoLocal;
    private String equipoVisitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="torneo_id")
    private Torneo torneo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "partido_arbitro",
        joinColumns = @JoinColumn(name = "partido_id"),
        inverseJoinColumns = @JoinColumn(name = "arbitro_id")
    )
    private List<Arbitro> arbitros = new ArrayList<>();

    // Getters y Setters
    public int getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(int idPartido) {
        this.idPartido = idPartido;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }



    public EstadoPartido getEstadoPartido() {
    return estadoPartido;
    }

    public void setEstadoPartido(EstadoPartido estadoPartido) {
        this.estadoPartido = estadoPartido;
    }

    public String getEquipoLocal() {
        return equipoLocal;
    }

    public void setEquipoLocal(String equipoLocal) {
        this.equipoLocal = equipoLocal;
    }

    public String getEquipoVisitante() {
        return equipoVisitante;
    }

    public void setEquipoVisitante(String equipoVisitante) {
        this.equipoVisitante = equipoVisitante;
    }

    public Torneo getTorneo() {
        return torneo;
    }

    public void setTorneo(Torneo torneo) {
        this.torneo = torneo;
    }

    public List<Arbitro> getArbitros() {
        return arbitros;
    }

    public void setArbitros(List<Arbitro> arbitros) {
        this.arbitros = arbitros;
    }
}
