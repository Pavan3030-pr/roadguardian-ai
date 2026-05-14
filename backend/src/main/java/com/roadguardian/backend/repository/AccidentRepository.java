package com.roadguardian.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.roadguardian.backend.model.entity.Accident;

import java.time.LocalDateTime;
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

	@Query("SELECT COUNT(a) FROM Accident a WHERE a.status = 'RESOLVED' OR a.status = 'CLOSED'")
	Long countResolvedAccidents();

	@Query("SELECT COUNT(a) FROM Accident a WHERE a.severity = 'CRITICAL'")
	Long countCriticalAccidents();

	@Query("SELECT COUNT(a) FROM Accident a WHERE a.createdAt >= :startDate AND a.createdAt <= :endDate")
	Long countAccidentsBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	Page<Accident> findBySeverity(Accident.SeverityLevel severity, Pageable pageable);

	Page<Accident> findByStatus(Accident.IncidentStatus status, Pageable pageable);

	Page<Accident> findByLocationNameContainingIgnoreCase(String locationName, Pageable pageable);

	@Query("SELECT a FROM Accident a WHERE a.createdAt >= :startDate ORDER BY a.createdAt DESC")
	List<Accident> findRecentAccidents(@Param("startDate") LocalDateTime startDate);

	@Query("SELECT a FROM Accident a WHERE " +
			"a.severity IN :severities AND " +
			"a.status IN :statuses AND " +
			"a.createdAt BETWEEN :startDate AND :endDate")
	Page<Accident> findByFilters(
			@Param("severities") List<Accident.SeverityLevel> severities,
			@Param("statuses") List<Accident.IncidentStatus> statuses,
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate,
			Pageable pageable
	);
}