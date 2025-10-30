package com.proyecto.cabapro.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.proyecto.cabapro.model.NewsArticle;
import com.proyecto.cabapro.service.NewsDataService;

@Controller
public class NewsViewController {

    private final NewsDataService newsDataService;

    public NewsViewController(NewsDataService newsDataService) {
        this.newsDataService = newsDataService;
    }

    @GetMapping("/noticias")
    public String showNews(Model model) {
        List<NewsArticle> articles = newsDataService.getBasketballNews();
        model.addAttribute("articles", articles);
        return "news"; // esto buscará un archivo llamado news.html
    }
}
