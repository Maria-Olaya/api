package com.proyecto.cabapro.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.proyecto.cabapro.model.NewsArticle;

@Service
public class NewsDataService {

    @Value("${newsdata.api.key}")
    private String apiKey;

    private final String BASE_URL = "https://newsdata.io/api/1/latest";
    private final RestTemplate restTemplate = new RestTemplate();

    public List<NewsArticle> getBasketballNews() {
        String url = BASE_URL + "?apikey=" + apiKey + "&q=NBA&category=sports&language=en";
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");

        List<NewsArticle> articles = new ArrayList<>();
        for (Map<String, Object> item : results) {
            articles.add(new NewsArticle(
                (String) item.get("title"),
                (String) item.get("description"),
                (String) item.get("link"),
                (String) item.get("image_url"),
                (String) item.get("source_name")
            ));
        }

        return articles;
    }
}
