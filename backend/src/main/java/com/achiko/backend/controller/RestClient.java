package com.achiko.backend.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.achiko.backend.dto.Sentences;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestClient {

    public static double sendPostRequest(String sentence1, String sentence2) {
        // String url = "http://localhost:8000/similarity";
        String url = "https://achiko.site/similarity";
        RestTemplate restTemplate = new RestTemplate();
        Sentences sentences = new Sentences(sentence1, sentence2);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Sentences> request = new HttpEntity<>(sentences, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            String responseBody = response.getBody();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("similarity_score").asDouble();

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return 0.0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}