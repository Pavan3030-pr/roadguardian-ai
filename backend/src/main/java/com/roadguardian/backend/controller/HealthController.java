package com.roadguardian.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {

	@GetMapping
	@Operation(summary = "Health check", description = "Check if service is running")
	public ResponseEntity<HealthResponse> health() {
		return ResponseEntity.ok(HealthResponse.builder()
				.status("UP")
				.message("RoadGuardian Backend is operational")
				.build());
	}

	@GetMapping("/ready")
	@Operation(summary = "Readiness check", description = "Check if service is ready for traffic")
	public ResponseEntity<HealthResponse> readiness() {
		return ResponseEntity.ok(HealthResponse.builder()
				.status("READY")
				.message("Service is ready")
				.build());
	}

	@lombok.Data
	@lombok.NoArgsConstructor
	@lombok.AllArgsConstructor
	@lombok.Builder
	public static class HealthResponse {
		private String status;
		private String message;
	}
}
