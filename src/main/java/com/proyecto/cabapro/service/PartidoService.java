package com.proyecto.cabapro.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.cabapro.controller.forms.PartidoForm;
import com.proyecto.cabapro.enums.EstadoPartido;
import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.model.Torneo;
import com.proyecto.cabapro.repository.PartidoRepository;

@Service
public class PartidoService {

    @Autowired
    private PartidoRepository partidoRepository;


    public List<Partido> getAllPartidos() {
        return partidoRepository.findAll();
    }

    public Optional<Partido> getPartidoById(int id) {
        return partidoRepository.findById(id);
    }




    // Reemplaza este método con validación
    public Partido savePartido(Partido partido) {
        var torneo = partido.getTorneo();
        if (torneo == null) {
            throw new IllegalArgumentException("Debe seleccionar un torneo");
        }

        if (torneo.getFechaInicio() != null && partido.getFecha().isBefore(torneo.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha del partido no puede ser antes del inicio del torneo");
        }
        if (torneo.getFechaFin() != null && partido.getFecha().isAfter(torneo.getFechaFin())) {
            throw new IllegalArgumentException("La fecha del partido no puede ser después de la finalización del torneo");
        }

        return partidoRepository.save(partido);
    }

    public Partido crearPartido(PartidoForm form, Torneo torneo) {
        Partido partido = new Partido();
        partido.setFecha(form.getFecha());
        partido.setLugar(form.getLugar());
        partido.setEquipoLocal(form.getEquipoLocal());
        partido.setEquipoVisitante(form.getEquipoVisitante());
        partido.setEstadoPartido(EstadoPartido.PROGRAMADO);
        partido.setTorneo(torneo);
        return savePartido(partido); // ya valida fechas
    }

    public Partido actualizarPartido(Partido partido, PartidoForm form) {
        partido.setFecha(form.getFecha());
        partido.setLugar(form.getLugar());
        partido.setEquipoLocal(form.getEquipoLocal());
        partido.setEquipoVisitante(form.getEquipoVisitante());
        partido.setEstadoPartido(form.getEstadoPartido());
        return savePartido(partido);
    }



    public void deletePartido(int id) {
        partidoRepository.deleteById(id);
    }

    public List<Partido> getPartidosByTorneo(int torneoId) {
        return partidoRepository.findByTorneo_IdTorneo(torneoId);
    }
    public List<Partido> obtenerPorTorneo(Torneo torneo) {
        return partidoRepository.findByTorneo_IdTorneo(torneo.getIdTorneo());
    }


    public List<Partido> getPartidosByArbitro(int arbitroId) {
    return partidoRepository.findByArbitros_Id(arbitroId);
    }


    public List<Partido> getPartidosByEstado(EstadoPartido estado) {
        List<Partido> partidos = partidoRepository.findByEstadoPartido(estado);
        partidos.forEach(this::actualizarEstado);
        return partidos;
    }

    public List<Partido> getPartidosByTorneoAndEstado(int torneoId, EstadoPartido estado) {
        List<Partido> partidos = partidoRepository.findByTorneo_IdTorneoAndEstadoPartido(torneoId, estado);
        partidos.forEach(this::actualizarEstado);
        return partidos;
    }

    public List<Partido> getPartidosByEquipoLocal(String equipoLocal) {
        return partidoRepository.findByEquipoLocal(equipoLocal);
    }

    public List<Partido> getPartidosByEquipoVisitante(String equipoVisitante) {
        return partidoRepository.findByEquipoVisitante(equipoVisitante);
    }

    public EstadoPartido calcularEstado(Partido partido) {
            actualizarEstado(partido); 
        return partido.getEstadoPartido();
    }

    
    // ---------------- Lógica de estado ----------------
    /**
     * Actualiza el estado del partido según la fecha actual.
     * - Si fecha == null -> no hace nada.
     * - Si ya CANCELADO -> no cambia.
     * - Usa duración por defecto de 120 minutos (ajusta para pruebas).
     */
  

    // ---- Nuevo método ----
    public void actualizarEstado(Partido partido) {
        if (partido.getFecha() == null) return;
        if (partido.getEstadoPartido() == EstadoPartido.CANCELADO) return;

        LocalDateTime ahora = LocalDateTime.now();
        long duracionMinutos = 20L; //  parametrizar

        LocalDateTime inicio = partido.getFecha();
        LocalDateTime fin = inicio.plusMinutes(duracionMinutos);

        if (ahora.isBefore(inicio)) {
            partido.setEstadoPartido(EstadoPartido.PROGRAMADO);
        } else if (!ahora.isBefore(inicio) && ahora.isBefore(fin)) {
            partido.setEstadoPartido(EstadoPartido.EN_CURSO);
        } else {
            partido.setEstadoPartido(EstadoPartido.FINALIZADO);
        }
    }



}
