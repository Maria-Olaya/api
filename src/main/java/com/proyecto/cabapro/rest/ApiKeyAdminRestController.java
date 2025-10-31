package com.proyecto.cabapro.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.cabapro.model.ApiKey;
import com.proyecto.cabapro.service.ApiKeyService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/admin/keys")
public class ApiKeyAdminRestController {

    private final ApiKeyService service;

    public ApiKeyAdminRestController(ApiKeyService service) {
        this.service = service;
    }

    @Operation(summary = "Genera una nueva API Key para una aplicación cliente")
    @PostMapping
    public ApiKey generateKey(@RequestParam String appName) {
        return service.generateForApp(appName);
    }
}
