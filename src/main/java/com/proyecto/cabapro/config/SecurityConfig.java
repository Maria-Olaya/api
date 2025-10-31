package com.proyecto.cabapro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.proyecto.cabapro.security.ApiKeyAuthFilter;
import com.proyecto.cabapro.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ApiKeyAuthFilter apiKeyAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, ApiKeyAuthFilter apiKeyAuthFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.apiKeyAuthFilter = apiKeyAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Swagger totalmente público
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api-docs/**",   // 👈 Cambiado aquí
                    "/api-docs/swagger-config", // 👈 Cambiado aquí
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/", "/error", "/favicon.ico"
                ).permitAll()

                // Endpoints públicos de autenticación
                .requestMatchers("/api/auth/**","/api/news").permitAll()

                // Rutas protegidas
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/arbitro/**").hasRole("ARBITRO")
                .requestMatchers("/api/**").authenticated()

                // Cualquier otro request (no listado) se bloquea
                .anyRequest().denyAll()
            )


            // 🔹 Orden importante: primero la API Key, luego el JWT
            .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class)
            // Filtro JWT
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            // Manejo de errores en JSON
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, excep) -> {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"error\": \"Unauthorized or invalid credentials\"}");
                })
            );

        return http.build();
    }
}
