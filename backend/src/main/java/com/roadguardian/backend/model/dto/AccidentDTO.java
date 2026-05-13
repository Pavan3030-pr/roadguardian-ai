package com.roadguardian.backend.model.dto;

import lombok.*;
import com.roadguardian.backend.model.entity.Accident;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccidentDTO {
	private Long id;
	private String title;
	private String description;
	private Double latitude;
	private Double longitude;
	private String locationName;
	private Accident.SeverityLevel severity;
	private Accident.IncidentStatus status;
	private Integer riskScore;
	private Integer casualties;
	private String imageUrl;
	private String videoUrl;
	private UserDTO reportedBy;
	private UserDTO ambulanceAssigned;
	private UserDTO policeAssigned;
	private UserDTO hospitalAssigned;
	private Long responseTimeMs;
	private String createdAt;
	private String updatedAt;
}
