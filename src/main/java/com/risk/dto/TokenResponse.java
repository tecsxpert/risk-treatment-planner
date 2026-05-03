package com.risk.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT bearer token returned after login or token refresh.")
public class TokenResponse {

    @Schema(
            description = "Signed JWT; send as Authorization: Bearer <token> on protected routes.",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhbmEiLCJyb2xlIjoiVklFV0VSIn0.sig"
    )
    private String token;

    public TokenResponse() {
    }

    public TokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
