package com.roadguardian.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roadguardian.backend.exception.ResourceNotFoundException;
import com.roadguardian.backend.model.dto.AccidentDTO;
import com.roadguardian.backend.model.dto.CreateAccidentRequest;
import com.roadguardian.backend.model.entity.Accident;
import com.roadguardian.backend.model.entity.User;
import com.roadguardian.backend.repository.AccidentRepository;
import com.roadguardian.backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccidentService {

	private final AccidentRepository accidentRepository;
	private final UserRepository userRepository;
	private final AIRiskEngineService aiRiskEngineService;

	public AccidentDTO createAccident(CreateAccidentRequest request, Long userId) {
		User reporter = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// Calculate risk score using AI engine
		Integer riskScore = aiRiskEngineService.calculateRiskScore(
				request.getSeverity(),
				request.getCasualties() != null ? request.getCasualties() : 0
		);

		Accident accident = Accident.builder()
				.title(request.getTitle())
				.description(request.getDescription())
				.latitude(request.getLatitude())
				.longitude(request.getLongitude())
				.locationName(request.getLocationName())
				.severity(Accident.SeverityLevel.valueOf(request.getSeverity().toUpperCase()))
				.status(Accident.IncidentStatus.REPORTED)
				.riskScore(riskScore)
				.casualties(request.getCasualties())
				.imageUrl(request.getImageUrl())
				.videoUrl(request.getVideoUrl())
				.reportedBy(reporter)
				.build();

		accident = accidentRepository.save(accident);
		log.info("Accident created with id: {} by user: {}", accident.getId(), userId);

		return convertToDTO(accident);
	}

	public AccidentDTO getAccidentById(Long id) {
		Accident accident = accidentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Accident not found with id: " + id));
		return convertToDTO(accident);
	}

	public List<AccidentDTO> getAllAccidents(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Accident> accidents = accidentRepository.findAll(pageable);
		return accidents.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public List<AccidentDTO> getAccidentsBySeverity(Accident.SeverityLevel severity) {
		return accidentRepository.findBySeverity(severity)
				.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	public List<AccidentDTO> getNearbyAccidents(Double latitude, Double longitude, Double radiusKm) {
		return accidentRepository.findNearbyAccidents(latitude, longitude, radiusKm)
				.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	public List<AccidentDTO> getActiveAccidents() {
		return accidentRepository.findActiveAccidents()
				.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	public AccidentDTO updateAccidentStatus(Long accidentId, Accident.IncidentStatus status) {
		Accident accident = accidentRepository.findById(accidentId)
				.orElseThrow(() -> new ResourceNotFoundException("Accident not found"));

		if (accident.getStatus() == Accident.IncidentStatus.CLOSED) {
			throw new IllegalStateException("Cannot update a closed accident");
		}

		accident.setStatus(status);
		if (status == Accident.IncidentStatus.RESOLVED) {
			accident.setResponseTimeMs(System.currentTimeMillis() - accident.getCreatedAt().toLocalTime().toNanoOfDay() / 1_000_000);
		}
		accident = accidentRepository.save(accident);

		return convertToDTO(accident);
	}

	public void deleteAccident(Long id) {
		Accident accident = accidentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Accident not found"));
		accidentRepository.delete(accident);
	}

	private AccidentDTO convertToDTO(Accident accident) {
		return AccidentDTO.builder()
				.id(accident.getId())
				.title(accident.getTitle())
				.description(accident.getDescription())
				.latitude(accident.getLatitude())
				.longitude(accident.getLongitude())
				.locationName(accident.getLocationName())
				.severity(accident.getSeverity())
				.status(accident.getStatus())
				.riskScore(accident.getRiskScore())
				.casualties(accident.getCasualties())
				.imageUrl(accident.getImageUrl())
				.videoUrl(accident.getVideoUrl())
				.responseTimeMs(accident.getResponseTimeMs())
				.createdAt(accident.getCreatedAt().toString())
				.updatedAt(accident.getUpdatedAt().toString())
				.build();
	}
}