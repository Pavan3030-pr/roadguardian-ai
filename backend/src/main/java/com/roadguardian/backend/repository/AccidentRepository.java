package com.roadguardian.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.roadguardian.backend.model.entity.Accident;

import java.util.List;

@Repository
public interface AccidentRepository extends JpaRepository<Accident, Long> {
	List<Accident> findBySeverity(Accident.SeverityLevel severity);
	List<Accident> findByStatus(Accident.IncidentStatus status);
	List<Accident> findByReportedById(Long userId);

	@Query("SELECT a FROM Accident a WHERE " +
			"(6371 * acos(cos(radians(:latitude)) * cos(radians(a.latitude)) * " +
			"cos(radians(a.longitude) - radians(:longitude)) + " +
			"sin(radians(:latitude)) * sin(radians(a.latitude)))) < :radiusKm")
	List<Accident> findNearbyAccidents(
			@Param("latitude") Double latitude,
			@Param("longitude") Double longitude,
			@Param("radiusKm") Double radiusKm
	);

	@Query("SELECT a FROM Accident a WHERE a.status NOT IN ('RESOLVED', 'CLOSED') " +
			"ORDER BY a.createdAt DESC")
	List<Accident> findActiveAccidents();
}