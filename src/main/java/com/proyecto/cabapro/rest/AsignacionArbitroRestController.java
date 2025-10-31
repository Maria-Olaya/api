// NUEVO - si 
package com.proyecto.cabapro.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.cabapro.service.AsignacionService;

@RestController
@RequestMapping("/api/arbitro/asignaciones")//bien
public class AsignacionArbitroRestController {

    private final AsignacionService asignacionService;

    public AsignacionArbitroRestController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    /**
     * Obtiene las asignaciones del árbitro actual autenticado.
     */
    @GetMapping
    public Map<String, Object> listarAsignaciones(@AuthenticationPrincipal User principal) {
        String correo = principal.getUsername();
        Map<String, Object> response = new HashMap<>();
        response.put("arbitro", asignacionService.getArbitroActual(correo));
        response.put("asignaciones", asignacionService.listarDelActual(correo));
        return response;
    }

    /**
     * Acepta una asignación del árbitro autenticado.
     */
    @PostMapping("/{id}/aceptar")
    public Map<String, String> aceptarAsignacion(@AuthenticationPrincipal User principal,
                                                 @PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            asignacionService.aceptar(principal.getUsername(), id);
            response.put("status", "success");
            response.put("message", "Asignación aceptada correctamente.");
        } catch (IllegalArgumentException ex) {
            response.put("status", "error");
            response.put("message", ex.getMessage());
        }
        return response;
    }

    /**
     * Rechaza una asignación del árbitro autenticado.
     */
    @PostMapping("/{id}/rechazar")
    public Map<String, String> rechazarAsignacion(@AuthenticationPrincipal User principal,
                                                  @PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            asignacionService.rechazar(principal.getUsername(), id);
            response.put("status", "success");
            response.put("message", "Asignación rechazada correctamente.");
        } catch (IllegalArgumentException ex) {
            response.put("status", "error");
            response.put("message", ex.getMessage());
        }
        return response;
    }
}
