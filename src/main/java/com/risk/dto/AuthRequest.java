package com.risk.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Credentials for registration or login.",
        example = "{\"username\":\"ana\",\"password\":\"secret123\"}"
)
public class AuthRequest {

    @Schema(description = "Unique username", example = "ana")
    private String username;

    @Schema(description = "Plain-text password (never logged or returned)", example = "secret123")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}