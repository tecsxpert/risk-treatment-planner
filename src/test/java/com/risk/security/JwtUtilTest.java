package com.risk.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {
    private final JwtUtil jwtUtil = new JwtUtil();

    @Test
    void testGenerateAndExtract() {
        String token = jwtUtil.generateToken("user1", "ADMIN");
        assertNotNull(token);
        assertEquals("user1", jwtUtil.extractUsername(token));
        assertEquals("ADMIN", jwtUtil.extractRole(token));
        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void testInvalidToken() {
        assertFalse(jwtUtil.isTokenValid("invalid.token.value"));
    }
}
