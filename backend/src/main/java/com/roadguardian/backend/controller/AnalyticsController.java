package com.roadguardian.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.roadguardian.backend.model.dto.DashboardMetricsDTO;
import com.roadguardian.backend.service.AnalyticsService;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Analytics and metrics endpoints")
public class AnalyticsController {

	private final AnalyticsService analyticsService;

	@GetMapping("/dashboard/metrics")
	@Operation(summary = "Get dashboard metrics", description = "Fetch overall system metrics")
	public ResponseEntity<DashboardMetricsDTO> getDashboardMetrics() {
		return ResponseEntity.ok(analyticsService.getDashboardMetrics());
	}
}
