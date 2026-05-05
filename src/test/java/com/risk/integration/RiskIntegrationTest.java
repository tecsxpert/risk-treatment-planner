package com.risk.integration;

import com.risk.entity.Risk;
import com.risk.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RiskIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil; // Inject utility to generate tokens

    @Test
    void testFullCrudFlow() {
        // Generate a real token for the test
        String token = "Bearer " + jwtUtil.generateToken("adminUser", "ADMIN");

        // Add token to headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        Risk newRisk = new Risk();
        newRisk.setTitle("Test Integration Risk");
        newRisk.setCategory("TECHNICAL");
        newRisk.setImpact(5);
        newRisk.setLikelihood(1);
        newRisk.setStatus("OPEN");

        // 1. CREATE
        HttpEntity<Risk> request = new HttpEntity<>(newRisk, headers);
        ResponseEntity<Risk> response = restTemplate.postForEntity("/api/risks/create", request, Risk.class);
        
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().getId()).isNotNull();

        // 2. READ
        Long riskId = response.getBody().getId();
        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<Risk> foundResponse = restTemplate.exchange("/api/risks/" + riskId, HttpMethod.GET, getRequest, Risk.class);
        
        assertThat(foundResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(foundResponse.getBody().getTitle()).isEqualTo("Test Integration Risk");
    }
}