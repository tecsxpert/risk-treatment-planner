package com.internship.tool.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder; // <-- Added specific import
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class AiServiceClient {

    private final RestTemplate restTemplate;

    // grab the python flask url from config, default to localhost:5000
    @Value("${ai.service.url:http://localhost:5000}")
    private String flaskUrl;

    // setup rest template with 10 sec timeout as per day 6 requirement
    public AiServiceClient(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }

    // helper method to hit the flask api so we dont repeat the same code 3 times
    private Map<String, Object> sendToFlask(String path, String userInput) {
        String fullUrl = flaskUrl + path;

        // creating the json body to send to python
        Map<String, String> body = new HashMap<>();
        body.put("text", userInput);

        try {
            System.out.println("sending request to python api: " + fullUrl);
            // making the actual post request
            return restTemplate.postForObject(fullUrl, body, Map.class);
            
        } catch (RestClientException e) { // <-- Changed to specific network exception
            // if python server is down or takes more than 10s, catch the error 
            // returning null here so the main java app doesnt crash
            System.out.println("error calling ai service: " + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> describeRisk(String text) {
        return sendToFlask("/describe", text);
    }

    public Map<String, Object> categoriseRisk(String text) {
        return sendToFlask("/categorise", text);
    }

    public Map<String, Object> generateReport(String text) {
        return sendToFlask("/generate-report", text);
    }
}