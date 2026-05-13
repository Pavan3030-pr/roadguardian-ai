package com.roadguardian.backend.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardMetricsDTO {
	private Integer totalAccidents;
	private Integer criticalCases;
	private Integer moderateCases;
	private Integer lowCases;
	private Integer resolvedCases;
	private Double averageResponseTime;
	private Integer totalAmbulances;
	private Integer totalPoliceUnits;
	private Integer totalHospitals;
}
