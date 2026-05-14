package com.roadguardian.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "accidents", indexes = {
		@Index(name = "idx_severity", columnList = "severity"),
		@Index(name = "idx_status", columnList = "status"),
		@Index(name = "idx_created_at", columnList = "created_at"),
		@Index(name = "idx_location", columnList = "latitude,longitude")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Accident {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Double longitude;

	@Column(nullable = false)
	private String locationName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SeverityLevel severity;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private IncidentStatus status;

	@Column(nullable = false)
	private Integer riskScore;

	private Integer casualties;

	private String imageUrl;

	private String videoUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reported_by_id", nullable = false)
	private User reportedBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ambulance_assigned_id")
	private User ambulanceAssigned;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "police_assigned_id")
	private User policeAssigned;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hospital_assigned_id")
	private User hospitalAssigned;

	private Long responseTimeMs;

	@Column(length = 50)
	private String weatherCondition;

	@Column(length = 50)
	private String trafficDensity;

	@Column(length = 50)
	private String roadType;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public enum SeverityLevel {
		LOW,
		MODERATE,
		HIGH,
		CRITICAL
	}

	public enum IncidentStatus {
		REPORTED,
		DISPATCHED,
		IN_PROGRESS,
		RESOLVED,
		ESCALATED,
		CLOSED
	}
}
