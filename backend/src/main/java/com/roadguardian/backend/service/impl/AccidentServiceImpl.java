package com.roadguardian.backend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roadguardian.backend.exception.ResourceNotFoundException;
import com.roadguardian.backend.model.dto.request.CreateAccidentRequest;
import com.roadguardian.backend.model.dto.request.UpdateAccidentRequest;
import com.roadguardian.backend.model.dto.response.AccidentResponse;
import com.roadguardian.backend.model.entity.Accident;
import com.roadguardian.backend.model.entity.User;
import com.roadguardian.backend.repository.AccidentRepository;
import com.roadguardian.backend.repository.UserRepository;
import com.roadguardian.backend.service.AccidentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccidentServiceImpl implements AccidentService {

	private final AccidentRepository accidentRepository;
	private final UserRepository userRepository;

	public AccidentResponse createAccident(CreateAccidentRequest request, Long reportedById) {
		User reportedBy = userRepository.findById(reportedById)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		Accident accident = Accident.builder()
				.title(request.getTitle())
				.description(request.getDescription())
				.latitude(request.getLatitude())
				.longitude(request.getLongitude())
				.locationName(request.getLocationName())
				.severity(Accident.SeverityLevel.valueOf(request.getSeverity().toUpperCase()))
				.status(Accident.IncidentStatus.REPORTED)
				.riskScore(calculateRiskScore(request))
				.casualties(request.getCasualties() != null ? request.getCasualties() : 0)
				.imageUrl(request.getImageUrl())
				.videoUrl(request.getVideoUrl())
				.reportedBy(reportedBy)
				.weatherCondition(request.getWeatherCondition())
				.trafficDensity(request.getTrafficDensity())
				.roadType(request.getRoadType())
				.build();

		accident = accidentRepository.save(accident);
		log.info("Accident created with ID: {}", accident.getId());

		return convertToResponse(accident);
	}

	public AccidentResponse updateAccident(Long accidentId, UpdateAccidentRequest request) {
		Accident accident = accidentRepository.findById(accidentId)
				.orElseThrow(() -> new ResourceNotFoundException("Accident not found with ID: " + accidentId));

		if (request.getTitle() != null) {
			accident.setTitle(request.getTitle());
		}
		if (request.getDescription() != null) {
			accident.setDescription(request.getDescription());
		}
		if (request.getSeverity() != null) {
			accident.setSeverity(Accident.SeverityLevel.valueOf(request.getSeverity().toUpperCase()));
		}
		if (request.getStatus() != null) {
			Accident.IncidentStatus oldStatus = accident.getStatus();
			accident.setStatus(Accident.IncidentStatus.valueOf(request.getStatus().toUpperCase()));

			if (!oldStatus.equals(accident.getStatus())) {
				long responseTime = LocalDateTime.now().getNano() - accident.getCreatedAt().getNano();
				accident.setResponseTimeMs(responseTime / 1_000_000);
			}
		}
		if (request.getCasualties() != null) {
			accident.setCasualties(request.getCasualties());
		}
		if (request.getImageUrl() != null) {
			accident.setImageUrl(request.getImageUrl());
		}
		if (request.getVideoUrl() != null) {
			accident.setVideoUrl(request.getVideoUrl());
		}
		if (request.getWeatherCondition() != null) {
			accident.setWeatherCondition(request.getWeatherCondition());
		}
		if (request.getTrafficDensity() != null) {
			accident.setTrafficDensity(request.getTrafficDensity());
		}
		if (request.getRoadType() != null) {
			accident.setRoadType(request.getRoadType());
		}

		accident = accidentRepository.save(accident);
		log.info("Accident updated with ID: {}", accidentId);

		return convertToResponse(accident);
	}

	public AccidentResponse getAccidentById(Long accidentId) {
		Accident accident = accidentRepository.findById(accidentId)
				.orElseThrow(() -> new ResourceNotFoundException("Accident not found with ID: " + accidentId));

		return convertToResponse(accident);
	}

	public List<AccidentResponse> getAllAccidents() {
		return accidentRepository.findAll().stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	public Page<AccidentResponse> getAccidentsPaginated(int pageNumber, int pageSize, String sortBy, String sortDirection) {
		Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));

		return accidentRepository.findAll(pageable)
				.map(this::convertToResponse);
	}

	public List<AccidentResponse> getAccidentsBySeverity(String severity) {
		Accident.SeverityLevel severityLevel = Accident.SeverityLevel.valueOf(severity.toUpperCase());
		return accidentRepository.findBySeverity(severityLevel).stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	public List<AccidentResponse> getAccidentsByStatus(String status) {
		Accident.IncidentStatus incidentStatus = Accident.IncidentStatus.valueOf(status.toUpperCase());
		return accidentRepository.findByStatus(incidentStatus).stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	public List<AccidentResponse> getNearbyAccidents(Double latitude, Double longitude, Double radiusKm) {
		return accidentRepository.findNearbyAccidents(latitude, longitude, radiusKm).stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	public List<AccidentResponse> getActiveAccidents() {
		return accidentRepository.findActiveAccidents().stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	public void assignAmbulance(Long accidentId, Long ambulanceUserId) {
		Accident accident = accidentRepository.findById(accidentId)
				.orElseThrow(() -> new ResourceNotFoundException("Accident not found"));

		User ambulance = userRepository.findById(ambulanceUserId)
				.orElseThrow(() -> new ResourceNotFoundException("Ambulance user not found"));

		accident.setAmbulanceAssigned(ambulance);
		accident.setStatus(Accident.IncidentStatus.DISPATCHED);
		accidentRepository.save(accident);

		log.info("Ambulance {} assigned to accident {}", ambulanceUserId, accidentId);
	}

	public void assignPolice(Long accidentId, Long policeUserId) {
		Accident accident = accidentRepository.findById(accidentId)
				.orElseThrow(() -> new ResourceNotFoundException("Accident not found"));

		User police = userRepository.findById(policeUserId)
				.orElseThrow(() -> new ResourceNotFoundException("Police user not found"));

		accident.setPoliceAssigned(police);
		accident.setStatus(Accident.IncidentStatus.DISPATCHED);
		accidentRepository.save(accident);

		log.info("Police {} assigned to accident {}", policeUserId, accidentId);
	}

	public void assignHospital(Long accidentId, Long hospitalUserId) {
		Accident accident = accidentRepository.findById(accidentId)
				.orElseThrow(() -> new ResourceNotFoundException("Accident not found"));

		User hospital = userRepository.findById(hospitalUserId)
				.orElseThrow(() -> new ResourceNotFoundException("Hospital user not found"));

		accident.setHospitalAssigned(hospital);
		accident.setStatus(Accident.IncidentStatus.DISPATCHED);
		accidentRepository.save(accident);

		log.info("Hospital {} assigned to accident {}", hospitalUserId, accidentId);
	}

	public void deleteAccident(Long accidentId) {
		Accident accident = accidentRepository.findById(accidentId)
				.orElseThrow(() -> new ResourceNotFoundException("Accident not found"));

		accidentRepository.delete(accident);
		log.info("Accident deleted with ID: {}", accidentId);
	}

	private int calculateRiskScore(CreateAccidentRequest request) {
		int score = 0;

		// Severity score
		switch (request.getSeverity().toUpperCase()) {
			case "CRITICAL":
				score += 40;
				break;
			case "HIGH":
				score += 30;
				break;
			case "MODERATE":
				score += 20;
				break;
			case "LOW":
				score += 10;
				break;
		}

		// Casualties score
		if (request.getCasualties() != null) {
			score += Math.min(request.getCasualties() * 5, 30);
		}

		// Traffic density
		if ("HIGH".equalsIgnoreCase(request.getTrafficDensity())) {
			score += 15;
		} else if ("MODERATE".equalsIgnoreCase(request.getTrafficDensity())) {
			score += 10;
		}

		// Weather condition
		if ("RAINY".equalsIgnoreCase(request.getWeatherCondition()) ||
			"FOGGY".equalsIgnoreCase(request.getWeatherCondition())) {
			score += 10;
		}

		return Math.min(score, 100);
	}

	private AccidentResponse convertToResponse(Accident accident) {
		return AccidentResponse.builder()
				.id(accident.getId())
				.title(accident.getTitle())
				.description(accident.getDescription())
				.latitude(accident.getLatitude())
				.longitude(accident.getLongitude())
				.locationName(accident.getLocationName())
				.severity(accident.getSeverity().toString())
				.status(accident.getStatus().toString())
				.riskScore(accident.getRiskScore())
				.casualties(accident.getCasualties())
				.imageUrl(accident.getImageUrl())
				.videoUrl(accident.getVideoUrl())
				.responseTimeMs(accident.getResponseTimeMs())
				.weatherCondition(accident.getWeatherCondition())
				.trafficDensity(accident.getTrafficDensity())
				.roadType(accident.getRoadType())
				.createdAt(accident.getCreatedAt())
				.updatedAt(accident.getUpdatedAt())
				.build();
	}
}
