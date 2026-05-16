package com.roadguardian.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "live_tracking", indexes = {
	@Index(name = "idx_live_tracking_response_id", columnList = "response_id"),
	@Index(name = "idx_live_tracking_created_at", columnList = "created_at")
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
    @JoinColumn(name = "response_id", nullable = false)
    private EmergencyResponse response;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION")
    private Double latitude;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION")
    private Double longitude;

    @Column(name = "speed_kmh")
    private Double speedKmh;

    @Column(name = "heading_degrees")
    private Double headingDegrees;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "accuracy_meters")
    private Double accuracyMeters;
}
