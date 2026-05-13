package com.roadguardian.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
		@Index(name = "idx_user_id", columnList = "user_id"),
		@Index(name = "idx_is_read", columnList = "is_read")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String message;

	@Enumerated(EnumType.STRING)
	private NotificationType type;

	@Column(nullable = false)
	private Boolean isRead = false;

	private Long accidentId;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public enum NotificationType {
		ACCIDENT_ALERT,
		DISPATCH_UPDATE,
		RESPONSE_NEEDED,
		ESCALATION_WARNING,
		SYSTEM_NOTIFICATION
	}
}
