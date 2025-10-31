package com.proyecto.cabapro.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.cabapro.controller.forms.RegisterForm;
import com.proyecto.cabapro.model.Usuario;
import com.proyecto.cabapro.repository.UsuarioRepository;
import com.proyecto.cabapro.security.JwtTokenProvider;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // --- LOGIN ---
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        String correo = request.get("correo");
        String contrasena = request.get("contrasena");

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtTokenProvider.createToken(usuario.getCorreo(), usuario.getRol());

        return Map.of(
                "token", token,
                "correo", usuario.getCorreo(),
                "rol", usuario.getRol()
        );
    }

    // --- REGISTRO ---
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody @Valid RegisterForm form) {

        // Validación de contraseñas
        if (!form.getContrasena().equals(form.getConfirmContrasena())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        // Validación de correo duplicado
        if (usuarioRepository.findByCorreo(form.getCorreo()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con ese correo");
        }

        // Creación del usuario
        Usuario nuevo = new Usuario();
        nuevo.setNombre(form.getNombre());
        nuevo.setApellido(form.getApellido());
        nuevo.setCorreo(form.getCorreo());
        nuevo.setContrasena(passwordEncoder.encode(form.getContrasena()));
        nuevo.setRol("ADMIN"); // Rol por defecto

        usuarioRepository.save(nuevo);

        // Generar token JWT al registrar
        String token = jwtTokenProvider.createToken(nuevo.getCorreo(), nuevo.getRol());

        return Map.of(
                "mensaje", "Usuario registrado correctamente",
                "correo", nuevo.getCorreo(),
                "rol", nuevo.getRol(),
                "token", token
        );
    }
}
