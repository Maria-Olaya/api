// NUEVO - si 
package com.proyecto.cabapro.rest;

import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.service.ArbitroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/arbitro")//bien
public class ArbitroRestController {

    private final ArbitroService arbitroService;

    @Autowired
    public ArbitroRestController(ArbitroService arbitroService) {
        this.arbitroService = arbitroService;
    }

    // ================= VER PERFIL =================
    @GetMapping("/perfil")
    public Map<String, Object> verPerfil(@AuthenticationPrincipal(expression = "username") String correo) {
        Arbitro arbitro = arbitroService.getActual(correo);
        Set<LocalDate> bloqueadas = arbitroService.fechasBloqueadas(arbitro);

        return Map.of(
                "arbitro", arbitro,
                "fechasBloqueadas", bloqueadas
        );
    }

    // ================= ACTUALIZAR PERFIL =================
    @PutMapping("/perfil")
    public Map<String, Object> actualizarPerfil(
            @AuthenticationPrincipal(expression = "username") String correo,
            @ModelAttribute Arbitro form,
            @RequestParam(value = "foto", required = false) MultipartFile foto,
            @RequestParam(value = "quitarFoto", required = false) Boolean quitarFoto
    ) {
        try {
            arbitroService.actualizarPerfil(
                    correo,
                    form.getUrlFoto(),
                    form.getFechasDisponibles(),
                    foto,
                    Boolean.TRUE.equals(quitarFoto)
            );

            return Map.of(
                    "status", "success",
                    "message", "Perfil actualizado correctamente"
            );

        } catch (IllegalArgumentException ex) {
            return Map.of(
                    "status", "error",
                    "message", "Error en los datos enviados",
                    "detail", ex.getMessage()
            );
        } catch (Exception ex) {
            return Map.of(
                    "status", "error",
                    "message", "Error al actualizar el perfil",
                    "detail", ex.getMessage()
            );
        }
    }
}
