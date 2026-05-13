package com.roadguardian.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.roadguardian.backend.model.dto.AIRiskRecommendationDTO;
import com.roadguardian.backend.service.AIRiskEngineService;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI Engine", description = "AI-powered risk and recommendation endpoints")
public class AIController {

	private final AIRiskEngineService aiRiskEngineService;

	@PostMapping("/recommend/{accidentId}")
	@Operation(summary = "Get AI recommendations", description = "Generate AI-based recommendations for an accident")
	public ResponseEntity<AIRiskRecommendationDTO> getRecommendations(@PathVariable Long accidentId) {
		return ResponseEntity.ok(aiRiskEngineService.generateRecommendations(accidentId));
	}
}
