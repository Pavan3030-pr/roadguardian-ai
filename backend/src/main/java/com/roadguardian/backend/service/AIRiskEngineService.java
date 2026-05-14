package com.roadguardian.backend.service;

import com.roadguardian.backend.model.dto.AIRiskRecommendationDTO;

public interface AIRiskEngineService {

	Integer calculateRiskScore(String severity, Integer casualties);

	AIRiskRecommendationDTO generateRecommendations(Long accidentId);
}
