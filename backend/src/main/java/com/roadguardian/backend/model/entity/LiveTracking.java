package com.roadguardian.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "live_tracking", indexes = {
		@Index(name = "idx_user_id", columnList = "user_id"),
		@Index(name = "idx_accident_id", columnList = "accident_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveTracking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "accident_id", nullable = false)
	private Accident accident;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Double longitude;

	@Column(nullable = false)
	private Double speed;

	private String heading;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime timestamp;
}
