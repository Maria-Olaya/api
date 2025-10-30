package com.proyecto.cabapro.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.cabapro.model.NewsArticle;
import com.proyecto.cabapro.service.NewsDataService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
public class NewsController {

    private final NewsDataService newsDataService;

    public NewsController(NewsDataService newsDataService) {
        this.newsDataService = newsDataService;
    }

    @Operation(
        summary = "Obtiene noticias de NBA",
        description = "Devuelve solo noticias de NBA en inglés filtradas por categoría deportes",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de noticias de NBA",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = NewsArticle.class)))
            )
        }
    )
    @GetMapping("/nba-news")
    public List<NewsArticle> fetchNBAnews() {
        return newsDataService.getBasketballNews();
    }
}

