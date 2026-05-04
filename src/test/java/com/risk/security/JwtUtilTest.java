package com.risk.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

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
