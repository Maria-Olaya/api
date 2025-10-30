// NUEVO - si 
package com.proyecto.cabapro.rest;

import com.proyecto.cabapro.controller.forms.PartidoForm;
import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.model.Torneo;
import com.proyecto.cabapro.service.PartidoService;
import com.proyecto.cabapro.service.TorneoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/partidos")
public class PartidoRestController {

    private final PartidoService partidoService;
    private final TorneoService torneoService;

    public PartidoRestController(PartidoService partidoService, TorneoService torneoService) {
        this.partidoService = partidoService;
        this.torneoService = torneoService;
    }

    // ✅ Obtener un partido por su ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPartido(@PathVariable("id") int id) {
        Optional<Partido> partidoOpt = partidoService.getPartidoById(id);
        if (partidoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El partido con ID " + id + " no existe");
        }
        return ResponseEntity.ok(partidoOpt.get());
    }

    // ✅ Editar/Actualizar un partido existente
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarPartido(
            @PathVariable("id") int id,
            @Valid @RequestBody PartidoForm partidoForm) {

        Optional<Partido> partidoOpt = partidoService.getPartidoById(id);
        if (partidoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El partido con ID " + id + " no existe");
        }

        Partido partido = partidoOpt.get();
        Torneo torneo = torneoService.obtenerPorId(partidoForm.getTorneoId());
        if (torneo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El torneo con ID " + partidoForm.getTorneoId() + " no existe");
        }

        // Actualizar datos
        partido.setFecha(partidoForm.getFecha());
        partido.setLugar(partidoForm.getLugar());
        partido.setEquipoLocal(partidoForm.getEquipoLocal());
        partido.setEquipoVisitante(partidoForm.getEquipoVisitante());
        partido.setTorneo(torneo);
        partido.setEstadoPartido(partidoForm.getEstadoPartido());

        try {
            partidoService.savePartido(partido);
            return ResponseEntity.ok(partido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ✅ Listar torneos activos (helper adaptado a REST)
    @GetMapping("/torneos-activos")
    public ResponseEntity<List<Torneo>> listarTorneosActivos() {
        List<Torneo> torneosActivos = torneoService.listarTorneos()
                .stream()
                .filter(t -> t.getFechaFin() != null && t.getFechaFin().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(torneosActivos);
    }
}
