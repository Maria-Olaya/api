package com.proyecto.cabapro.controller.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.cabapro.controller.forms.PartidoForm;
import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.model.Torneo;
import com.proyecto.cabapro.service.PartidoService;
import com.proyecto.cabapro.service.TorneoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/torneos/{torneoId}/partidos")
@Tag(name = "Partidos", description = "Operaciones sobre partidos de un torneo")
public class PartidoRestController {

    private final PartidoService partidoService;
    private final TorneoService torneoService;

    public PartidoRestController(PartidoService partidoService, TorneoService torneoService) {
        this.partidoService = partidoService;
        this.torneoService = torneoService;
    }

    @GetMapping
    @Operation(summary = "Lista todos los partidos de un torneo")
    public ResponseEntity<List<Partido>> listarPartidos(@PathVariable int torneoId) {
        List<Partido> partidos = partidoService.getPartidosByTorneo(torneoId);
        return ResponseEntity.ok(partidos);
    }

    @GetMapping("/{partidoId}")
    @Operation(summary = "Obtiene un partido por ID dentro de un torneo")
    public ResponseEntity<Partido> obtenerPartido(@PathVariable int torneoId, @PathVariable int partidoId) {
        return partidoService.getPartidoById(partidoId)
                .filter(p -> p.getTorneo().getIdTorneo() == torneoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crea un nuevo partido en un torneo")
    public ResponseEntity<Partido> crearPartido(@PathVariable int torneoId, 
                                                @Valid @RequestBody PartidoForm form) {
        Torneo torneo = torneoService.obtenerPorId(torneoId);
        if (torneo == null) return ResponseEntity.notFound().build();

        Partido partido = partidoService.crearPartido(form, torneo);
        return ResponseEntity.status(HttpStatus.CREATED).body(partido);
    }

    @PutMapping("/{partidoId}")
    @Operation(summary = "Actualiza un partido existente de un torneo")
    public ResponseEntity<Partido> actualizarPartido(@PathVariable int torneoId,
                                                     @PathVariable int partidoId,
                                                     @Valid @RequestBody PartidoForm form) {
        return partidoService.getPartidoById(partidoId)
                .filter(p -> p.getTorneo().getIdTorneo() == torneoId)
                .map(p -> ResponseEntity.ok(partidoService.actualizarPartido(p, form)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{partidoId}")
    @Operation(summary = "Elimina un partido de un torneo")
    public ResponseEntity<Void> eliminarPartido(@PathVariable int torneoId,
                                                @PathVariable int partidoId) {
        return partidoService.getPartidoById(partidoId)
                .filter(p -> p.getTorneo().getIdTorneo() == torneoId)
                .map(p -> {
                    partidoService.deletePartido(partidoId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
