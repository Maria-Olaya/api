package com.proyecto.cabapro.controller;

import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.service.ArbitroService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Set;

@Controller
@RequestMapping("/arbitro/edit")
public class ArbitroController {

    private final ArbitroService arbitroService;

    public ArbitroController(ArbitroService arbitroService) {
        this.arbitroService = arbitroService;
    }

    // GET: muestra el formulario con los datos actuales y las fechas bloqueadas
    @GetMapping
    public String verPerfil(@AuthenticationPrincipal(expression = "username") String correo, Model model) {
        Arbitro arbitro = arbitroService.getActual(correo);

        // Fechas bloqueadas (asignaciones ACEPTADAS) para pintar en la vista
        Set<LocalDate> bloqueadas = arbitroService.fechasBloqueadas(arbitro);

        model.addAttribute("arbitro", arbitro);
        model.addAttribute("bloqueadas", bloqueadas);
        return "arbitro/perfil"; // templates/arbitro/perfil.html
    }

    // POST: procesa el form y delega la actualización al Service.
    @PostMapping
    public String actualizarPerfil(@AuthenticationPrincipal(expression = "username") String correo,
                                   @ModelAttribute("arbitro") Arbitro form,
                                   @RequestParam(value = "foto", required = false) MultipartFile foto,
                                   @RequestParam(value = "quitarFoto", required = false) Boolean quitarFoto,
                                   BindingResult binding,
                                   Model model,
                                   RedirectAttributes ra) {
        if (binding.hasErrors()) {
            Arbitro actual = arbitroService.getActual(correo);
            Set<LocalDate> bloqueadas = arbitroService.fechasBloqueadas(actual);
            model.addAttribute("bloqueadas", bloqueadas);
            return "arbitro/perfil";
        }

        try {
            // Mantengo compatibilidad: si no envían archivo y no quieren quitar,
            // se conserva la URL que venía en el hidden urlFoto
            arbitroService.actualizarPerfil(
                    correo,
                    form.getUrlFoto(),
                    form.getFechasDisponibles(),
                    foto,
                    Boolean.TRUE.equals(quitarFoto)
            );
            ra.addFlashAttribute("msgCode", "perfil.actualizado");
            return "redirect:/arbitro/dashboard";
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("errCode", "perfil.error");
            ra.addFlashAttribute("errArg0", ex.getMessage());
            return "redirect:/arbitro/perfil";
        } catch (Exception ex) {
            ra.addFlashAttribute("errCode", "perfil.error");
            ra.addFlashAttribute("errArg0", ex.getMessage());
            return "redirect:/arbitro/perfil";
        }
    }
}
