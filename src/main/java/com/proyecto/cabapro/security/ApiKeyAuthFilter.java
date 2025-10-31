package com.proyecto.cabapro.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.proyecto.cabapro.service.ApiKeyService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final ApiKeyService apiKeyService;

    public ApiKeyAuthFilter(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {
        // 🔹 Define path desde el request
        String path = request.getRequestURI();

        // Ignorar Swagger
         if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") ||
            path.startsWith("/api-docs") || path.startsWith("/swagger-resources") ||
            path.startsWith("/webjars")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader("X-API-KEY");

        if (apiKey != null && apiKeyService.isValid(apiKey)) {
            filterChain.doFilter(request, response);
        } else if (request.getRequestURI().startsWith("/api/public")) {
            // Permite rutas públicas sin API key
        
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or missing API key");
        }
    }
}
