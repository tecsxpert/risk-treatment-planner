package com.risk.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_JWT = "bearer-jwt";

    @Bean
    public OpenAPI riskTreatmentPlannerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Risk Treatment Planner API")
                        .version("0.0.1")
                        .description("REST API for risk register operations, file upload, and JWT authentication."))
                .components(new Components()
                        .addSecuritySchemes(BEARER_JWT, new SecurityScheme()
                                .name(BEARER_JWT)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Obtain a token from POST /auth/login, then send: Authorization: Bearer <token>")));
    }
}
