// NUEVO - si 
package com.proyecto.cabapro.rest;

import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.service.ArbitroService;
import com.proyecto.cabapro.service.LiquidacionService;
import com.proyecto.cabapro.service.LiquidacionService.DuplicateLiquidacionException;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/admin/liquidaciones")
public class LiquidacionAdminRestController {

    private final ArbitroService arbitroService;
    private final LiquidacionService liquidacionService;
    private final MessageSource messageSource;

    public LiquidacionAdminRestController(ArbitroService arbitroService,
                                          LiquidacionService liquidacionService,
                                          MessageSource messageSource) {
        this.arbitroService = arbitroService;
        this.liquidacionService = liquidacionService;
        this.messageSource = messageSource;
    }

    // ✅ Listar todas las liquidaciones o las de un árbitro específico
    @GetMapping
    public ResponseEntity<?> listar(@RequestParam(required = false) Integer arbitroId) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, Object> response = new HashMap<>();

        List<Arbitro> arbitros = arbitroService.listar();
        response.put("arbitros", arbitros);

        if (arbitroId != null) {
            Arbitro a = arbitroService.buscar(arbitroId);
            if (a != null) {
                response.put("arbitro", a);
                response.put("liquidaciones", liquidacionService.listarPorArbitro(arbitroId));
            } else {
                String errMsg = messageSource.getMessage(
                        "error.arbitroNoEncontradoId",
                        new Object[]{arbitroId},
                        locale
                );
                response.put("error", errMsg);
            }
        }
        return ResponseEntity.ok(response);
    }

    // ✅ Generar liquidación para un árbitro
    @PostMapping("/{arbitroId}/generar")
    public ResponseEntity<?> generar(@PathVariable Integer arbitroId) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, Object> response = new HashMap<>();

        try {
            var liq = liquidacionService.generarParaArbitro(arbitroId);
            String msg = messageSource.getMessage(
                    "msg.liquidacionGenerada",
                    new Object[]{liq.getId(), liq.getTotal()},
                    locale
            );
            response.put("message", msg);
            response.put("liquidacion", liq);
            return ResponseEntity.ok(response);
        } catch (DuplicateLiquidacionException d) {
            response.put("error", d.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (IllegalStateException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = messageSource.getMessage(
                    "error.generico",
                    new Object[]{e.getMessage()},
                    locale
            );
            response.put("error", msg);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // ✅ Marcar una liquidación como pagada
    @PostMapping("/{liqId}/pagar")
    public ResponseEntity<?> pagar(@PathVariable Long liqId, @RequestParam Integer arbitroId) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, Object> response = new HashMap<>();

        try {
            liquidacionService.pagar(liqId);
            String msg = messageSource.getMessage(
                    "msg.liquidacionPagada",
                    new Object[]{liqId},
                    locale
            );
            response.put("message", msg);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String msg = messageSource.getMessage(
                    "error.alPagar",
                    new Object[]{e.getMessage()},
                    locale
            );
            response.put("error", msg);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // ✅ Descargar PDF de una liquidación
    @GetMapping("/{liqId}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Long liqId) {
        try {
            byte[] pdf = liquidacionService.obtenerPdf(liqId);
            String fileName = URLEncoder.encode("liquidacion-" + liqId + ".pdf", StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName)
                    .body(pdf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
