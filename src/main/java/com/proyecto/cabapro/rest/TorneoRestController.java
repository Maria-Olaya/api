// NUEVO - si 
package com.proyecto.cabapro.rest;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import com.proyecto.cabapro.controller.forms.PartidoForm;
import com.proyecto.cabapro.controller.forms.TorneoForm;
import com.proyecto.cabapro.enums.CategoriaTorneo;
import com.proyecto.cabapro.enums.TipoTorneo;
import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.model.Torneo;
import com.proyecto.cabapro.service.PartidoService;
import com.proyecto.cabapro.service.TorneoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/torneos")
public class TorneoRestController {

    @Autowired
    private MessageSource messageSource;

    private final TorneoService torneoService;
    private final PartidoService partidoService;

    public TorneoRestController(TorneoService torneoService, PartidoService partidoService) {
        this.torneoService = torneoService;
        this.partidoService = partidoService;
    }

    // ✅ Listar todos los torneos
    @GetMapping
    public List<Torneo> listar() {
        List<Torneo> torneos = torneoService.listarTorneos();

        // Evitar ciclos infinitos
        torneos.forEach(t -> {
            if (t.getPartidos() != null) {
                t.getPartidos().forEach(p -> p.setTorneo(null));
            }
        });

        return torneos;
    }

    // ✅ Obtener detalle de un torneo (con sus partidos)
    @GetMapping("/{id}")
    public Object verDetalle(@PathVariable("id") int id) {
        Torneo torneo = torneoService.obtenerPorId(id);
        if (torneo == null) {
            return Map.of("error", "Torneo no encontrado");
        }

        List<Partido> partidos = partidoService.obtenerPorTorneo(torneo);
        if (partidos != null) {
            partidos.forEach(p -> p.setTorneo(null));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("torneo", torneo);
        response.put("partidos", partidos);
        return response;
    }

    // ✅ Crear torneo nuevo
    @PostMapping
    public Object guardar(@Valid @RequestBody TorneoForm torneoForm) {
        Torneo torneo = new Torneo();
        torneo.setNombre(torneoForm.getNombre());
        torneo.setTipoTorneo(torneoForm.getTipoTorneo());
        torneo.setCategoria(torneoForm.getCategoria());
        torneo.setFechaInicio(torneoForm.getFechaInicio());
        torneo.setFechaFin(torneoForm.getFechaFin());

        torneoService.guardarTorneo(torneo);
        return Map.of("message", "Torneo creado correctamente", "torneo", torneo);
    }

    // ✅ Actualizar torneo
    @PutMapping("/{id}")
    public Object actualizar(@PathVariable("id") int id, @Valid @RequestBody TorneoForm torneoForm) {
        Torneo torneo = torneoService.obtenerPorId(id);
        if (torneo == null) {
            return Map.of("error", "Torneo no encontrado");
        }

        torneo.setNombre(torneoForm.getNombre());
        torneo.setTipoTorneo(torneoForm.getTipoTorneo());
        torneo.setCategoria(torneoForm.getCategoria());
        torneo.setFechaInicio(torneoForm.getFechaInicio());
        torneo.setFechaFin(torneoForm.getFechaFin());

        torneoService.guardarTorneo(torneo);
        return Map.of("message", "Torneo actualizado correctamente", "torneo", torneo);
    }

    // ✅ Eliminar torneo
    @DeleteMapping("/{id}")
    public Object eliminar(@PathVariable("id") int id) {
        torneoService.eliminarTorneo(id);
        return Map.of("message", "Torneo eliminado correctamente");
    }

    // -----------------------------------------------------------------
    // ✅ PARTIDOS DENTRO DEL TORNEO
    // -----------------------------------------------------------------

    // Listar partidos del torneo
    @GetMapping("/{torneoId}/partidos")
    public Object listarPartidos(@PathVariable int torneoId) {
        Torneo torneo = torneoService.obtenerPorId(torneoId);
        if (torneo == null) {
            return Map.of("error", "Torneo no encontrado");
        }

        List<Partido> partidos = partidoService.obtenerPorTorneo(torneo);
        if (partidos != null) {
            partidos.forEach(p -> p.setTorneo(null));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("torneo", torneo);
        response.put("partidos", partidos);
        return response;
    }

    // Crear partido dentro de un torneo
    @PostMapping("/{torneoId}/partidos")
    public Object guardarPartido(@PathVariable int torneoId, @Valid @RequestBody PartidoForm partidoForm) {
        Torneo torneo = torneoService.obtenerPorId(torneoId);
        if (torneo == null) {
            return Map.of("error", "Torneo no encontrado");
        }

        try {
            partidoService.crearPartido(partidoForm, torneo);
            return Map.of("message", "Partido creado correctamente");
        } catch (IllegalArgumentException ex) {
            String mensaje = messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), LocaleContextHolder.getLocale());
            return Map.of("error", mensaje);
        }
    }

    // Actualizar partido
    @PutMapping("/{torneoId}/partidos/{partidoId}")
    public Object actualizarPartido(@PathVariable int torneoId,
                                    @PathVariable int partidoId,
                                    @Valid @RequestBody PartidoForm partidoForm) {

        Partido partido = partidoService.getPartidoById(partidoId).orElse(null);
        if (partido == null || partido.getTorneo().getIdTorneo() != torneoId) {
            return Map.of("error", "Partido o torneo inválido");
        }

        try {
            partidoService.actualizarPartido(partido, partidoForm);
            return Map.of("message", "Partido actualizado correctamente");
        } catch (IllegalArgumentException ex) {
            String mensaje = messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), LocaleContextHolder.getLocale());
            return Map.of("error", mensaje);
        }
    }

    // Eliminar partido
    @DeleteMapping("/{torneoId}/partidos/{partidoId}")
    public Object eliminarPartido(@PathVariable int torneoId,
                                  @PathVariable int partidoId) {
        partidoService.deletePartido(partidoId);
        return Map.of("message", "Partido eliminado correctamente");
    }

    // -----------------------------------------------------------------
    // ✅ Endpoints de enums (Tipos y Categorías)
    // -----------------------------------------------------------------
    @GetMapping("/tipos")
    public TipoTorneo[] tiposTorneo() {
        return TipoTorneo.values();
    }

    @GetMapping("/categorias")
    public CategoriaTorneo[] categoriasTorneo() {
        return CategoriaTorneo.values();
    }
}
