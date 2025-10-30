// NUEVO - si 
package com.proyecto.cabapro.rest;

import com.proyecto.cabapro.dto.TarifaCalculoRow;
import com.proyecto.cabapro.enums.Escalafon;
import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.model.Torneo;
import com.proyecto.cabapro.service.PartidoService;
import com.proyecto.cabapro.service.TarifaService;
import com.proyecto.cabapro.service.TorneoService;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import java.util.Locale;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/admin/tarifas")
public class TarifaAdminRestController {

    private final TorneoService torneoService;
    private final PartidoService partidoService;
    private final TarifaService tarifaService;
    private final MessageSource messageSource;

    @Autowired
    public TarifaAdminRestController(
            TorneoService torneoService,
            PartidoService partidoService,
            TarifaService tarifaService,
            MessageSource messageSource) {
        this.torneoService = torneoService;
        this.partidoService = partidoService;
        this.tarifaService = tarifaService;
        this.messageSource = messageSource;
    }

    // ✅ Reemplaza al método "asignar" original, pero ahora devuelve JSON
    @GetMapping("/asignar")
    public Object asignar(@RequestParam(required = false) Integer torneoId, Locale locale) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("torneos", torneoService.listarTorneos());

        if (torneoId == null) {
            response.put("message", "Debe especificar un torneoId para generar tarifas.");
            return response;
        }

        Torneo torneo = torneoService.obtenerPorId(torneoId);
        if (torneo == null) {
            String errorMsg = messageSource.getMessage("error.torneoNoEncontrado", null, locale);
            response.put("error", errorMsg);
            return response;
        }

        tarifaService.generarAutomaticoParaTorneo(torneoId);

        List<Partido> partidos = partidoService.getPartidosByTorneo(torneo.getIdTorneo());

        List<TarifaCalculoRow> filas = new ArrayList<>();
        Map<Integer, BigDecimal> totalesPorArbitro = new LinkedHashMap<>();
        Map<Integer, Arbitro> arbitroById = new HashMap<>();

        for (Partido p : partidos) {
            BigDecimal base = tarifaService.baseCategoria(torneo.getCategoria());
            for (Arbitro a : p.getArbitros()) {
                Escalafon esc = a.getEscalafon();
                BigDecimal adicional = tarifaService.adicionalEscalafon(torneo.getCategoria(), esc);
                BigDecimal total = tarifaService.totalPor(torneo.getCategoria(), esc);

                filas.add(new TarifaCalculoRow(p, a, base, adicional, total));

                arbitroById.putIfAbsent(a.getId(), a);
                totalesPorArbitro.merge(a.getId(), total, BigDecimal::add);
            }
        }

        List<Map<String, Object>> resumen = new ArrayList<>();
        totalesPorArbitro.forEach((id, total) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("arbitro", arbitroById.get(id));
            item.put("total", total);
            resumen.add(item);
        });

        response.put("torneo", torneo);
        response.put("filas", filas);
        response.put("resumen", resumen);

        return response;
    }
}
