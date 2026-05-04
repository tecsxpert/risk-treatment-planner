package com.risk.controller;

import com.risk.dto.AuthRequest;
import com.risk.dto.TokenResponse;
import com.risk.entity.User;
import com.risk.repository.UserRepository;
import com.risk.security.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Register, login, and refresh JWT tokens")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a user with role VIEWER. Fails if the username is already taken.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class, example = "User registered successfully"))),
            @ApiResponse(responseCode = "400", description = "Username already exists",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class, example = "User already exists")))
    })
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("VIEWER");

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Returns a JWT for the given credentials.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "JWT issued",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid password",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class, example = "Invalid password"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class, example = "User not found")))
    })
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {

        User user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Issues a new JWT using the current user's role from the database (token subject must still be valid).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "New JWT issued",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Missing or invalid Authorization header or JWT",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class, example = "Invalid token"))),
            @ApiResponse(responseCode = "404", description = "User no longer exists",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class, example = "User not found")))
    })
    public ResponseEntity<?> refresh(
            @Parameter(description = "Bearer access token", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String header) {

        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body("Invalid token");
        }

        String token = header.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(400).body("Invalid token");
        }

        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        String newToken = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return ResponseEntity.ok(new TokenResponse(newToken));
    }
}
