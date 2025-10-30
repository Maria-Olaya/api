// NUEVO - si 
package com.proyecto.cabapro.rest;

import com.proyecto.cabapro.controller.forms.RegisterForm;
import com.proyecto.cabapro.model.Administrador;
import com.proyecto.cabapro.repository.UsuarioRepository;
import com.proyecto.cabapro.service.CustomUserDetailsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private MessageSource messageSource;

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthRestController(UsuarioRepository usuarioRepository, CustomUserDetailsService customUserDetailsService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * Endpoint de prueba para login (solo retorna mensaje, el login real lo maneja Spring Security).
     */
    @GetMapping("/login")
    public Map<String, String> login() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Endpoint de autenticación. El login se gestiona por Spring Security.");
        return response;
    }

    /**
     * Registrar nuevo usuario administrador.
     */
    @PostMapping("/registro")
    public Map<String, String> registrarUsuario(@Valid @RequestBody RegisterForm form) {
        Map<String, String> response = new HashMap<>();

        // Validar coincidencia de contraseñas
        if (!form.getContrasena().equals(form.getConfirmContrasena())) {
            response.put("status", "error");
            response.put("message", messageSource.getMessage(
                    "registro.error.contrasena_no_coincide", null, LocaleContextHolder.getLocale()));
            return response;
        }

        // Validar correo duplicado
        if (customUserDetailsService.correoExiste(form.getCorreo())) {
            response.put("status", "error");
            response.put("message", messageSource.getMessage(
                    "registro.error.correo_duplicado", null, LocaleContextHolder.getLocale()));
            return response;
        }

        // Crear usuario administrador
        Administrador admin = new Administrador(
                form.getNombre(),
                form.getApellido(),
                form.getCorreo(),
                passwordEncoder.encode(form.getContrasena()),
                "ROLE_ADMIN"
        );

        usuarioRepository.save(admin);

        response.put("status", "success");
        response.put("message", messageSource.getMessage(
                "registro.exito", null, LocaleContextHolder.getLocale()));

        return response;
    }
}
