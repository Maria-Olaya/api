// NUEVO - si 
package com.proyecto.cabapro.rest;

import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.service.ArbitroService;
import com.proyecto.cabapro.service.LiquidacionService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/arbitro/liquidaciones")
public class LiquidacionArbitroRestController {

    private final ArbitroService arbitroService;
    private final LiquidacionService liquidacionService;

    public LiquidacionArbitroRestController(ArbitroService arbitroService,
                                            LiquidacionService liquidacionService) {
        this.arbitroService = arbitroService;
        this.liquidacionService = liquidacionService;
    }

    /**
     * 🔹 Obtiene las liquidaciones del árbitro autenticado
     */
    @GetMapping
    public ResponseEntity<?> listarMisLiquidaciones(@AuthenticationPrincipal User principal) {
        try {
            Arbitro arbitro = arbitroService.getActual(principal.getUsername());
            List<?> liquidaciones = liquidacionService.listarPorArbitro(arbitro.getId());
            return ResponseEntity.ok(Map.of(
                    "arbitroId", arbitro.getId(),
                    "cantidad", liquidaciones.size(),
                    "liquidaciones", liquidaciones
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "No se pudieron obtener las liquidaciones",
                            "detalle", e.getMessage()
                    ));
        }
    }

    /**
     * 🔹 Devuelve el PDF de una liquidación específica del árbitro autenticado
     */
    @GetMapping("/{liqId}/pdf")
    public ResponseEntity<Resource> obtenerPdf(@PathVariable Long liqId) {
        try {
            byte[] pdf = liquidacionService.obtenerPdf(liqId);

            if (pdf == null || pdf.length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }

            String filename = URLEncoder.encode("mi-liquidacion-" + liqId + ".pdf", StandardCharsets.UTF_8);
            ByteArrayResource resource = new ByteArrayResource(pdf);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
                    .contentLength(pdf.length)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
