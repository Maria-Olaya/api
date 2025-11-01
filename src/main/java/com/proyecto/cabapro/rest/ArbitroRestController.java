// NUEVO - si 
package com.proyecto.cabapro.rest;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.service.ArbitroService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/arbitro")//bien
public class ArbitroRestController {

    private final ArbitroService arbitroService;

    @Autowired
    public ArbitroRestController(ArbitroService arbitroService) {
        this.arbitroService = arbitroService;
    }

    // ================= VER PERFIL =================
     // ================= VER PERFIL =================
    @Operation(
        summary = "Ver perfil del árbitro actual",
        description = "Obtiene la información del árbitro autenticado junto con las fechas bloqueadas.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Perfil obtenido correctamente",
                content = @Content(
                    schema = @Schema(
                        example = "{ \"arbitro\": { \"nombre\": \"Carlos\", \"apellido\": \"Ramirez\", \"correo\": \"carlos.ramirez@liga.com\", \"especialidad\": \"PRINCIPAL\", \"escalafon\": \"REGIONAL\" }, \"fechasBloqueadas\": [\"2025-11-05\",\"2025-11-10\"] }"
                    )
                )
            )
        }
    )
    @GetMapping("/perfil")
    public Map<String, Object> verPerfilverPerfil(
            @AuthenticationPrincipal(expression = "username")
            @Parameter(description = "Correo del árbitro autenticado", example = "carlos.ramirez@liga.com")
            String correo
    ){
        Arbitro arbitro = arbitroService.getActual(correo);
        Set<LocalDate> bloqueadas = arbitroService.fechasBloqueadas(arbitro);

        return Map.of(
                "arbitro", arbitro,
                "fechasBloqueadas", bloqueadas
        );
    }

    // ================= ACTUALIZAR PERFIL =================
    // ================= ACTUALIZAR PERFIL =================
    @Operation(
        summary = "Actualizar perfil del árbitro",
        description = "Permite actualizar el perfil del árbitro, incluyendo foto, fechas disponibles y otros datos.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Perfil actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos enviados"),
            @ApiResponse(responseCode = "500", description = "Error al actualizar el perfil")
        }
    )
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
