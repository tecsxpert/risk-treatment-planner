package com.risk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.risk.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RiskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String viewerToken;
    private String managerToken;
    private String adminToken;

    @BeforeEach
    public void setup() {
        viewerToken = "Bearer " + jwtUtil.generateToken("viewer1", "VIEWER");
        managerToken = "Bearer " + jwtUtil.generateToken("manager1", "MANAGER");
        adminToken = "Bearer " + jwtUtil.generateToken("admin1", "ADMIN");
    }

    @Test
    public void testFullCrudFlow_WithH2() throws Exception {
        
        String newRiskJson = """
                {
                    "title": "CRUD Test Risk",
                    "category": "OPERATIONAL",
                    "likelihood": 4,
                    "impact": 5,
                    "status": "OPEN"
                }
                """;

        MvcResult createResult = mockMvc.perform(post("/api/risks/create")
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newRiskJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("CRUD Test Risk"))
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        Integer createdId = objectMapper.readTree(responseBody).get("id").asInt();

        mockMvc.perform(get("/api/risks/" + createdId)
                .header("Authorization", viewerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("CRUD Test Risk"));

        String updateJson = """
                {
                    "title": "CRUD Test Risk Updated",
                    "category": "OPERATIONAL",
                    "likelihood": 2,
                    "impact": 2,
                    "status": "MITIGATED"
                }
                """;

        mockMvc.perform(put("/api/risks/" + createdId)
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("MITIGATED"));

        // Fixed to expect 204 No Content
        mockMvc.perform(delete("/api/risks/" + createdId)
                .header("Authorization", adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/risks/" + createdId)
                .header("Authorization", viewerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAll_NoToken_Returns403() throws Exception {
        mockMvc.perform(get("/api/risks/all")).andExpect(status().isForbidden());
    }

    @Test
    public void testGetAll_WithToken_Returns200() throws Exception {
        mockMvc.perform(get("/api/risks/all").header("Authorization", viewerToken)).andExpect(status().isOk()).andExpect(jsonPath("$.content").exists());
    }

    @Test
    public void testGetAll_WithPagination_Returns200() throws Exception {
        mockMvc.perform(get("/api/risks/all?page=0&size=5&sortBy=id&sortDir=asc").header("Authorization", viewerToken)).andExpect(status().isOk());
    }

    @Test
    public void testGetById_NotFound_Returns404() throws Exception {
        mockMvc.perform(get("/api/risks/99999").header("Authorization", viewerToken)).andExpect(status().isNotFound());
    }

    @Test
    public void testCreate_NoToken_Returns403() throws Exception {
        mockMvc.perform(post("/api/risks/create").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Risk\",\"category\":\"OPERATIONAL\",\"likelihood\":3,\"impact\":3,\"status\":\"OPEN\"}")).andExpect(status().isForbidden());
    }

    @Test
    public void testCreate_ViewerToken_Returns403() throws Exception {
        mockMvc.perform(post("/api/risks/create").header("Authorization", viewerToken).contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Risk\",\"category\":\"OPERATIONAL\",\"likelihood\":3,\"impact\":3,\"status\":\"OPEN\"}")).andExpect(status().isForbidden());
    }

    @Test
    public void testCreate_ManagerToken_Returns201() throws Exception {
        mockMvc.perform(post("/api/risks/create").header("Authorization", managerToken).contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Manager Risk\",\"category\":\"OPERATIONAL\",\"likelihood\":2,\"impact\":3,\"status\":\"OPEN\"}")).andExpect(status().isCreated()).andExpect(jsonPath("$.title").value("Manager Risk"));
    }

    @Test
    public void testCreate_AdminToken_Returns201() throws Exception {
        mockMvc.perform(post("/api/risks/create").header("Authorization", adminToken).contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Admin Risk\",\"category\":\"FINANCIAL\",\"likelihood\":1,\"impact\":5,\"status\":\"OPEN\"}")).andExpect(status().isCreated()).andExpect(jsonPath("$.title").value("Admin Risk"));
    }

    @Test
    public void testDelete_ViewerToken_Returns403() throws Exception {
        mockMvc.perform(delete("/api/risks/1").header("Authorization", viewerToken)).andExpect(status().isForbidden());
    }

    @Test
    public void testExport_WithToken_Returns200() throws Exception {
        mockMvc.perform(get("/api/risks/export").header("Authorization", viewerToken)).andExpect(status().isOk()).andExpect(header().string("Content-Disposition", containsString("risks.csv")));
    }
}