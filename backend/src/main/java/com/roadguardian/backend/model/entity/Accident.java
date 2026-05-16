package com.roadguardian.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "accidents", indexes = {
	@Index(name = "idx_accidents_severity", columnList = "severity"),
	@Index(name = "idx_accidents_status", columnList = "status"),
	@Index(name = "idx_accidents_created_at", columnList = "created_at"),
	@Index(name = "idx_accidents_location", columnList = "latitude,longitude")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Accident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_id", nullable = false)
    private User reportedBy;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION")
    private Double latitude;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION")
    private Double longitude;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private Integer estimatedCasualties;

    @Enumerated(EnumType.STRING)
    private WeatherCondition weatherCondition;

    @Enumerated(EnumType.STRING)
    private TrafficDensity trafficDensity;

    @Enumerated(EnumType.STRING)
    private RoadType roadType;

    @Column(name = "ai_risk_score")
    private Double aiRiskScore;

    @Column(name = "ambulance_required")
    private Boolean ambulanceRequired;

    @Column(name = "police_alert_level", length = 50)
    private String policeAlertLevel;

    @Column(name = "nearby_hospital_id")
    private Long nearbyHospitalId;

    @OneToMany(mappedBy = "accident", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmergencyResponse> emergencyResponses = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "video_url")
    private String videoUrl;

    public enum Severity {
        LOW, MODERATE, HIGH, CRITICAL
    }

    public enum Status {
        REPORTED, ACKNOWLEDGED, IN_PROGRESS, RESOLVED, CANCELLED
    }

    public enum WeatherCondition {
        CLEAR, RAINY, FOGGY, STORMY, SNOWY
    }

    public enum TrafficDensity {
        LOW, MODERATE, HIGH, VERY_HIGH
    }

    public enum RoadType {
        HIGHWAY, CITY, RURAL, ARTERIAL
    }
}
