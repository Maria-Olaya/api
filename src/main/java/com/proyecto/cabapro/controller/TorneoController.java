package com.proyecto.cabapro.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.proyecto.cabapro.controller.forms.PartidoForm;
import com.proyecto.cabapro.controller.forms.TorneoForm;
import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.model.Torneo;
import com.proyecto.cabapro.service.PartidoService;
import com.proyecto.cabapro.service.TorneoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/torneos")
public class TorneoController {

    private final TorneoService torneoService;
    PartidoService partidoService;

    public TorneoController(TorneoService torneoService, PartidoService partidoService) {
        this.torneoService = torneoService;
        this.partidoService = partidoService;
    }

    // Mostrar lista de torneos
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("torneos", torneoService.listarTorneos());
        return "torneos/lista";
    }

    // Formulario de creación
    @GetMapping("/nuevo")
    public String mostrarFormNuevo(Model model) {
        model.addAttribute("torneoForm", new TorneoForm());
        return "torneos/form";
    }

    // Guardar torneo nuevo
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("torneoForm") TorneoForm torneoForm,
                          BindingResult result) {
        if (result.hasErrors()) {
            return "torneos/form";
        }

        Torneo torneo = new Torneo();
        torneo.setNombre(torneoForm.getNombre());
        torneo.setTipoTorneo(torneoForm.getTipoTorneo());
        torneo.setCategoria(torneoForm.getCategoria());
        torneo.setFechaInicio(torneoForm.getFechaInicio());
        torneo.setFechaFin(torneoForm.getFechaFin());

        torneoService.guardarTorneo(torneo);
        return "redirect:/torneos";
    }

    // Mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String mostrarFormEditar(@PathVariable("id") int id, Model model) {
        Torneo torneo = torneoService.obtenerPorId(id);
        if (torneo == null) {
            return "redirect:/torneos";
        }

        TorneoForm torneoForm = new TorneoForm();
        torneoForm.setIdTorneo(torneo.getIdTorneo());
        torneoForm.setNombre(torneo.getNombre());
        torneoForm.setTipoTorneo(torneo.getTipoTorneo());
        torneoForm.setCategoria(torneo.getCategoria());
        torneoForm.setFechaInicio(torneo.getFechaInicio());
        torneoForm.setFechaFin(torneo.getFechaFin());

        model.addAttribute("torneoForm", torneoForm);
        return "torneos/form";
    }

    // Actualizar torneo existente
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable("id") int id,
                             @Valid @ModelAttribute("torneoForm") TorneoForm torneoForm,
                             BindingResult result) {
        if (result.hasErrors()) {
            return "torneos/form";
        }

        Torneo torneo = torneoService.obtenerPorId(id);
        if (torneo == null) {
            // Si el torneo no existe, redirigir a la lista de torneos
            
            return "redirect:/torneos";
        }

        torneo.setNombre(torneoForm.getNombre());
        torneo.setTipoTorneo(torneoForm.getTipoTorneo());
        torneo.setCategoria(torneoForm.getCategoria());
        torneo.setFechaInicio(torneoForm.getFechaInicio());
        torneo.setFechaFin(torneoForm.getFechaFin());

        torneoService.guardarTorneo(torneo);
        return "redirect:/torneos";
    }

    // Eliminar torneo
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") int id) {
        torneoService.eliminarTorneo(id);
        return "redirect:/torneos";
    }

  
    // -------------------------------------------------- Partido dentro de Torneo ------------------------------------------------- /
  
  
    // Ver detalle de torneo + partidos
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable("id") int id, Model model) {
        Torneo torneo = torneoService.obtenerPorId(id);
        if (torneo == null) {
            return "redirect:/torneos";
        }

        // Aquí cargas los partidos asociados al torneo
        List<Partido> partidos = partidoService.obtenerPorTorneo(torneo);
        model.addAttribute("partidos", partidos);


        model.addAttribute("torneo", torneo);
        model.addAttribute("partidos", partidos);
        return "torneos/detalle"; // vista donde muestras torneo + partidos
    }


    @GetMapping("/{torneoId}/partidos/nuevo")
    public String mostrarFormNuevoPartido(@PathVariable int torneoId, Model model) {
        PartidoForm partidoForm = new PartidoForm();
        partidoForm.setTorneoId(torneoId); // asigna el torneo
        model.addAttribute("partidoForm", partidoForm);
        return "partidos/form";
    }

    @PostMapping("/{torneoId}/partidos/guardar")
    public String guardarPartido(@PathVariable int torneoId,
                                @Valid @ModelAttribute("partidoForm") PartidoForm partidoForm,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            return "partidos/form";
        }
        Torneo torneo = torneoService.obtenerPorId(torneoId);
        if (torneo == null) return "redirect:/torneos";
    
            try {
                partidoService.crearPartido(partidoForm, torneo);
                return "redirect:/torneos/" + torneoId;
            } catch (IllegalArgumentException ex) {
                model.addAttribute("errorMessage", ex.getMessage());
                return "partidos/form";
            }

        
    }
   
    @GetMapping("/{torneoId}/partidos/editar/{partidoId}")
    public String mostrarFormEditarPartido(@PathVariable int torneoId,
                                        @PathVariable int partidoId,
                                        Model model) {
        Partido partido = partidoService.getPartidoById(partidoId).orElse(null);
        if (partido == null || partido.getTorneo().getIdTorneo() != torneoId) {
            return "redirect:/torneos/" + torneoId;
        }

        PartidoForm partidoForm = new PartidoForm();
        partidoForm.setIdPartido(partido.getIdPartido());
        partidoForm.setFecha(partido.getFecha());
        partidoForm.setLugar(partido.getLugar());
        partidoForm.setEstadoPartido(partido.getEstadoPartido());
        partidoForm.setEquipoLocal(partido.getEquipoLocal());
        partidoForm.setEquipoVisitante(partido.getEquipoVisitante());
        partidoForm.setTorneoId(torneoId);

        model.addAttribute("partidoForm", partidoForm);
        return "partidos/form";
    }


    @PostMapping("/{torneoId}/partidos/{partidoId}/actualizar")
    public String actualizarPartido(@PathVariable int torneoId,
                                    @PathVariable int partidoId,
                                    @Valid @ModelAttribute("partidoForm") PartidoForm partidoForm,
                                    BindingResult result,
                                    Model model) {

        if (result.hasErrors()) {
            return "partidos/form";
        }

        Partido partido = partidoService.getPartidoById(partidoId).orElse(null);
        if (partido == null || partido.getTorneo().getIdTorneo() != torneoId) {
            return "redirect:/torneos/" + torneoId;
        }

        try {
            partidoService.actualizarPartido(partido, partidoForm);
            return "redirect:/torneos/" + torneoId;
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "partidos/form";
        }

    }

    @GetMapping("/{torneoId}/partidos/eliminar/{partidoId}")
    public String eliminarPartido(@PathVariable int torneoId,
                                @PathVariable int partidoId) {
        partidoService.deletePartido(partidoId);
        return "redirect:/torneos/" + torneoId;
    }


}
