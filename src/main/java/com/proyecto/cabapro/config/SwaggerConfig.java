package com.proyecto.cabapro.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
            .info(new Info()
                .title("CABA Pro API")
                .description("API REST para gestión de torneos, árbitros y partidos del sistema CABA Pro")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Equipo Inspire AI / CABA Pro")
                    .email("soporte@cabapro.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("http://springdoc.org")));
    }
}
