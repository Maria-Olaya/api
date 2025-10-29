// NUEVO 
package com.proyecto.cabapro.controller;

import com.proyecto.cabapro.dto.AsignacionDTO;
import com.proyecto.cabapro.dto.PartidoDTO;
import com.proyecto.cabapro.dto.TorneoDTO;
import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.model.Asignacion;
import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.model.Torneo;
import com.proyecto.cabapro.service.ArbitroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// ✅ Controlador REST para Árbitros
@RestController
@RequestMapping("/api/arbitros")
public class ArbitroRestController {

    @Autowired
    private ArbitroService arbitroService;

    // 🔹 Listar todos los árbitros
    @GetMapping
    public List<ArbitroDTO> listarArbitros() {
        List<Arbitro> arbitros = arbitroService.listar();
        return arbitros.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 🔹 Obtener árbitro por ID (con sus asignaciones, partidos y torneos)
    @GetMapping("/{id}")
    public ArbitroDTO obtenerPorId(@PathVariable Integer id) {
        Arbitro arbitro = arbitroService.buscar(id);
        if (arbitro == null) {
            throw new RuntimeException("Árbitro no encontrado con id: " + id);
        }
        return convertToDTO(arbitro);
    }

    // =======================
    // 🔸 Conversión de entidad a DTO
    // =======================
    private ArbitroDTO convertToDTO(Arbitro arbitro) {
        ArbitroDTO dto = new ArbitroDTO();
        dto.setId(arbitro.getId());
        dto.setNombre(arbitro.getNombre());
        dto.setApellido(arbitro.getApellido());
        dto.setCorreo(arbitro.getCorreo());
        dto.setRol(arbitro.getRol());
        dto.setEspecialidad(arbitro.getEspecialidad().name());
        dto.setEscalafon(arbitro.getEscalafon().name());
        dto.setUrlFoto(arbitro.getUrlFoto());
        dto.setFechasDisponibles(arbitro.getFechasDisponibles());

        // 🔹 Convertir asignaciones a DTO
        dto.setAsignaciones(arbitro.getAsignaciones().stream()
                .map(this::convertAsignacionToDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    // =======================
    // 🔸 Conversión de Asignación a DTO
    // =======================
    private AsignacionDTO convertAsignacionToDTO(Asignacion asignacion) {
        AsignacionDTO dto = new AsignacionDTO();
        dto.setId(asignacion.getId());
        dto.setFechaAsignacion(asignacion.getFechaAsignacion());
        dto.setEstado(asignacion.getEstado());
        dto.setMonto(asignacion.getMonto());

        // 🔹 Partido asociado
        Partido partido = asignacion.getPartido();
        if (partido != null) {
            PartidoDTO partidoDTO = new PartidoDTO();
            partidoDTO.setIdPartido(partido.getIdPartido());
            partidoDTO.setFecha(partido.getFecha());
            partidoDTO.setLugar(partido.getLugar());
            partidoDTO.setEstadoPartido(partido.getEstadoPartido());
            partidoDTO.setEquipoLocal(partido.getEquipoLocal()); 
            partidoDTO.setEquipoVisitante(partido.getEquipoVisitante());
            dto.setPartido(partidoDTO);
        }

        // 🔹 Torneo asociado
        Torneo torneo = asignacion.getTorneo();
        if (torneo != null) {
            TorneoDTO torneoDTO = new TorneoDTO();
            torneoDTO.setIdTorneo(torneo.getIdTorneo());
            torneoDTO.setNombre(torneo.getNombre());
            torneoDTO.setTipoTorneo(torneo.getTipoTorneo());
            torneoDTO.setCategoria(torneo.getCategoria());
            torneoDTO.setFechaInicio(torneo.getFechaInicio());
            torneoDTO.setFechaFin(torneo.getFechaFin());
            dto.setTorneo(torneoDTO);
        }

        return dto;
    }

    // =======================
    // 🔸 DTO Interno del Árbitro
    // =======================
    static class ArbitroDTO {
        private Integer id;
        private String nombre;
        private String apellido;
        private String correo;
        private String rol;
        private String especialidad;
        private String escalafon;
        private String urlFoto;
        private Object fechasDisponibles; // Set<LocalDate>
        private List<AsignacionDTO> asignaciones;

        // Getters y Setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

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

        public Object getFechasDisponibles() { return fechasDisponibles; }
        public void setFechasDisponibles(Object fechasDisponibles) { this.fechasDisponibles = fechasDisponibles; }

        public List<AsignacionDTO> getAsignaciones() { return asignaciones; }
        public void setAsignaciones(List<AsignacionDTO> asignaciones) { this.asignaciones = asignaciones; }
    }
}
