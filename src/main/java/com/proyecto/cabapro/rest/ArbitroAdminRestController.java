// NUEVO - si
package com.proyecto.cabapro.rest;

import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.model.Asignacion;
import com.proyecto.cabapro.service.ArbitroService;
import com.proyecto.cabapro.service.AsignacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/arbitros")
public class ArbitroAdminRestController {

    private final ArbitroService service;
    private final AsignacionService asignacionService;

    @Autowired
    public ArbitroAdminRestController(ArbitroService service, AsignacionService asignacionService) {
        this.service = service;
        this.asignacionService = asignacionService;
    }

    // ================= LISTAR TODOS =================
    @GetMapping
    public List<Arbitro> listarArbitros() {
        return service.listar();
    }

    // ================= BUSCAR POR ID =================
    @GetMapping("/{id}")
    public Arbitro obtenerArbitro(@PathVariable Integer id) {
        return service.buscar(id);
    }

    // ================= CREAR =================
    @PostMapping
    public Arbitro crearArbitro(@RequestBody Arbitro arbitro) {
        return service.crear(arbitro);
    }

    // ================= ACTUALIZAR =================
    @PutMapping("/{id}")
    public Arbitro actualizarArbitro(@PathVariable Integer id, @RequestBody Arbitro arbitro) {
        return service.actualizar(id, arbitro);
    }

    // ================= ELIMINAR =================
    @DeleteMapping("/{id}")
    public void eliminarArbitro(@PathVariable Integer id) {
        service.eliminar(id);
    }

    // ================= ASIGNACIONES DE UN ÁRBITRO =================
    @GetMapping("/{id}/asignaciones")
    public List<Asignacion> obtenerAsignacionesPorArbitro(@PathVariable Integer id) {
        return asignacionService.listarPorArbitroId(id);
    }
}
