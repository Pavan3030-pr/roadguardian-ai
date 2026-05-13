package com.roadguardian.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.roadguardian.backend.model.entity.AIRecommendation;
import java.util.Optional;

@Repository
public interface AIRecommendationRepository extends JpaRepository<AIRecommendation, Long> {
	Optional<AIRecommendation> findByAccidentId(Long accidentId);
}
