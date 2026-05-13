package com.roadguardian.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.roadguardian.backend.model.dto.JwtTokenResponse;
import com.roadguardian.backend.model.dto.LoginRequest;
import com.roadguardian.backend.model.dto.RegisterRequest;
import com.roadguardian.backend.model.dto.UserDTO;
import com.roadguardian.backend.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	@Operation(summary = "User login", description = "Authenticate user and get JWT token")
	public ResponseEntity<JwtTokenResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@PostMapping("/register")
	@Operation(summary = "User registration", description = "Register new user")
	public ResponseEntity<JwtTokenResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.ok(authService.register(request));
	}

	@GetMapping("/user/{id}")
	@Operation(summary = "Get user details", description = "Fetch user information by ID")
	public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
		return ResponseEntity.ok(authService.getUserById(id));
	}
}
