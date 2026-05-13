package com.roadguardian.backend.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccidentRequest {
	@NotBlank(message = "Title is required")
	private String title;

	private String description;

	@NotNull(message = "Latitude is required")
	@DecimalMin("-90")
	@DecimalMax("90")
	private Double latitude;

	@NotNull(message = "Longitude is required")
	@DecimalMin("-180")
	@DecimalMax("180")
	private Double longitude;

	@NotBlank(message = "Location name is required")
	private String locationName;

	@NotBlank(message = "Severity is required")
	private String severity;

	private Integer casualties;

	private String imageUrl;

	private String videoUrl;
}
