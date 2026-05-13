package com.roadguardian.backend.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveTrackingDTO {
	private Long id;
	private Long userId;
	private String userName;
	private Double latitude;
	private Double longitude;
	private Double speed;
	private String heading;
	private String timestamp;
}
