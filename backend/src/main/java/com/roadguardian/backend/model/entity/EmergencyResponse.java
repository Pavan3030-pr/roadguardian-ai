package com.roadguardian.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_responses", indexes = {
		@Index(name = "idx_accident_id", columnList = "accident_id"),
		@Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyResponse {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "accident_id", nullable = false)
	private Accident accident;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ResponseType type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ResponseStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assigned_to_id")
	private User assignedTo;

	private String remarks;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public enum ResponseType {
		AMBULANCE,
		POLICE,
		HOSPITAL,
		FIRE_BRIGADE
	}

	public enum ResponseStatus {
		DISPATCHED,
		IN_TRANSIT,
		ARRIVED,
		COMPLETED,
		CANCELLED
	}
}
