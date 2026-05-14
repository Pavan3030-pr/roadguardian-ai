package com.roadguardian.backend.model.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIRecommendationResponse {

    private Long id;

    private Long accidentId;

    private String ambulanceNeeded;

    private String hospitalRequired;

    private String policeAlertLevel;

    private String roadblockRequired;

    private Integer confidenceScore;

    private String riskLevel;

    private String escalationRecommendation;

    private String weatherConsideration;

    private String trafficManagementAdvice;
}
