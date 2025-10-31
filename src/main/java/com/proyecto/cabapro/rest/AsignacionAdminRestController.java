// NUEVO - si
package com.proyecto.cabapro.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.cabapro.model.Asignacion;
import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.service.AsignacionService;

@RestController
@RequestMapping("/api/admin/asignaciones")//bien
public class AsignacionAdminRestController {

    private final AsignacionService asignacionService;

    public AsignacionAdminRestController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    /**
     * Devuelve los datos necesarios para crear una asignación.
     * Incluye árbitros disponibles, partido, y listas de apoyo.
     */
    @GetMapping("/crear")
    public Map<String, Object> obtenerDatosCreacion(@RequestParam("partidoId") int partidoId) {
        Map<String, Object> datos = new HashMap<>();

        Partido partido = asignacionService.buscarPartido(partidoId);
        datos.put("partido", partido);
        datos.put("arbitros", asignacionService.listarArbitros());
        datos.put("noDisponibles", asignacionService.arbitrosNoDisponiblesIds(partidoId));
        datos.put("yaAsignados", asignacionService.arbitrosYaAsignadosIds(partidoId));
        datos.put("aceptadas", asignacionService.listarAceptadasPorPartido(partidoId));
        datos.put("faltanEspecialidades", asignacionService.especialidadesFaltantes(partidoId));
        datos.put("especialidadesOcupadas", asignacionService.especialidadesOcupadas(partidoId));

        return datos;
    }

    /**
     * Crea una nueva asignación para un partido dado.
     */
    @PostMapping("/uno")//???
    public Map<String, Object> crearAsignacion(
            @RequestParam("partidoId") int partidoId,
            @RequestParam("arbitroId") Integer arbitroId
    ) {
        Map<String, Object> respuesta = new HashMap<>();
        try {
            Asignacion asignacion = asignacionService.crearParaArbitroYPartido(arbitroId, partidoId);
            respuesta.put("mensaje", "Asignación creada correctamente");
            respuesta.put("idAsignacion", asignacion.getId());
            respuesta.put("status", "success");
        } catch (IllegalArgumentException e) {
            respuesta.put("mensaje", "Error al crear la asignación: " + e.getMessage());
            respuesta.put("status", "error");
        }
        return respuesta;
    }
}
