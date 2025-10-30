package com.proyecto.cabapro.controller.api;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.cabapro.model.Torneo;
import com.proyecto.cabapro.service.TorneoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/torneos")
@CrossOrigin(origins = "*")
@Tag(
    name = "Gestión de Torneos",
    description = "Endpoints para crear, listar, buscar, actualizar y eliminar torneos del sistema de arbitraje."
)
public class TorneoRestController {

    private final TorneoService torneoService;

    @Autowired
    public TorneoRestController(TorneoService torneoService) {
        this.torneoService = torneoService;
    }

    // ✅ Listar todos los torneos
    @Operation(summary = "Listar todos los torneos", description = "Devuelve una lista con todos los torneos registrados en el sistema.")
    @ApiResponse(
        responseCode = "200",
        description = "Lista de torneos obtenida exitosamente.",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Torneo.class))
    )
    @GetMapping
    public List<Torneo> listarTorneos() {
        return torneoService.listarTorneos();
    }

    // ✅ Obtener un torneo por ID
    @Operation(summary = "Obtener un torneo por su ID", description = "Devuelve la información completa de un torneo dado su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Torneo encontrado."),
        @ApiResponse(responseCode = "404", description = "Torneo no encontrado.")
    })
    @GetMapping("/{id}")
    public Torneo obtenerTorneo(
            @Parameter(description = "ID del torneo que se desea consultar.", example = "1")
            @PathVariable int id) {

        Torneo torneo = torneoService.obtenerPorId(id);
        if (torneo == null) {
            throw new RuntimeException("Torneo no encontrado con id " + id);
        }
        return torneo;
    }

    // ✅ Buscar por nombre
    @Operation(summary = "Buscar un torneo por nombre", description = "Permite buscar un torneo existente utilizando su nombre exacto.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Torneo encontrado."),
        @ApiResponse(responseCode = "404", description = "No se encontró un torneo con ese nombre.")
    })
    @GetMapping("/buscar")
    public Optional<Torneo> buscarPorNombre(
            @Parameter(description = "Nombre exacto del torneo a buscar.", example = "Copa Primavera")
            @RequestParam String nombre) {
        return torneoService.obtenerPorNombre(nombre);
    }

    // ✅ Crear nuevo torneo
    @Operation(summary = "Crear un nuevo torneo", description = "Registra un nuevo torneo en el sistema con los datos proporcionados.")
    @ApiResponse(responseCode = "201", description = "Torneo creado exitosamente.")
    @PostMapping
    public Torneo crearTorneo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos del torneo a crear.",
                required = true,
                content = @Content(schema = @Schema(implementation = Torneo.class))
            )
            @RequestBody Torneo torneo) {
        return torneoService.guardarTorneo(torneo);
    }

    // ✅ Actualizar torneo
    @Operation(summary = "Actualizar un torneo existente", description = "Modifica los datos de un torneo existente usando su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Torneo actualizado correctamente."),
        @ApiResponse(responseCode = "404", description = "No existe torneo con ese ID.")
    })
    @PutMapping("/{id}")
    public Torneo actualizarTorneo(
            @Parameter(description = "ID del torneo a actualizar.", example = "1")
            @PathVariable int id,
            @RequestBody Torneo torneoActualizado) {

        Torneo torneo = torneoService.obtenerPorId(id);
        if (torneo == null) {
            throw new RuntimeException("No existe torneo con id " + id);
        }

        torneo.setNombre(torneoActualizado.getNombre());
        torneo.setTipoTorneo(torneoActualizado.getTipoTorneo());
        torneo.setCategoria(torneoActualizado.getCategoria());
        torneo.setFechaInicio(torneoActualizado.getFechaInicio());
        torneo.setFechaFin(torneoActualizado.getFechaFin());

        return torneoService.guardarTorneo(torneo);
    }

    // ✅ Eliminar torneo
    @Operation(summary = "Eliminar un torneo", description = "Elimina un torneo existente del sistema usando su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Torneo eliminado exitosamente."),
        @ApiResponse(responseCode = "404", description = "No se encontró el torneo especificado.")
    })
    @DeleteMapping("/{id}")
    public void eliminarTorneo(
            @Parameter(description = "ID del torneo a eliminar.", example = "1")
            @PathVariable int id) {
        torneoService.eliminarTorneo(id);
    }
}
