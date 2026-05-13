package com.roadguardian.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.roadguardian.backend.model.entity.LiveTracking;
import java.util.List;

@Repository
public interface LiveTrackingRepository extends JpaRepository<LiveTracking, Long> {
	List<LiveTracking> findByUserId(Long userId);
	List<LiveTracking> findByAccidentId(Long accidentId);
}
