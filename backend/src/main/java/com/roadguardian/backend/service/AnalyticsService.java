package com.roadguardian.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roadguardian.backend.model.dto.DashboardMetricsDTO;
import com.roadguardian.backend.model.entity.Accident;
import com.roadguardian.backend.model.entity.User;
import com.roadguardian.backend.repository.AccidentRepository;
import com.roadguardian.backend.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnalyticsService {

	private final AccidentRepository accidentRepository;
	private final UserRepository userRepository;

	public DashboardMetricsDTO getDashboardMetrics() {
		List<Accident> allAccidents = accidentRepository.findAll();

		int critical = (int) allAccidents.stream()
				.filter(a -> a.getSeverity() == Accident.SeverityLevel.CRITICAL)
				.count();

		int moderate = (int) allAccidents.stream()
				.filter(a -> a.getSeverity() == Accident.SeverityLevel.MODERATE)
				.count();

		int low = (int) allAccidents.stream()
				.filter(a -> a.getSeverity() == Accident.SeverityLevel.LOW)
				.count();

		int resolved = (int) allAccidents.stream()
				.filter(a -> a.getStatus() == Accident.IncidentStatus.RESOLVED ||
						a.getStatus() == Accident.IncidentStatus.CLOSED)
				.count();

		double avgResponseTime = allAccidents.stream()
				.filter(a -> a.getResponseTimeMs() != null)
				.mapToLong(Accident::getResponseTimeMs)
				.average()
				.orElse(0);

		int ambulances = (int) userRepository.findByRole(User.UserRole.AMBULANCE).size();
		int police = (int) userRepository.findByRole(User.UserRole.POLICE).size();
		int hospitals = (int) userRepository.findByRole(User.UserRole.HOSPITAL).size();

		return DashboardMetricsDTO.builder()
				.totalAccidents(allAccidents.size())
				.criticalCases(critical)
				.moderateCases(moderate)
				.lowCases(low)
				.resolvedCases(resolved)
				.averageResponseTime(avgResponseTime)
				.totalAmbulances(ambulances)
				.totalPoliceUnits(police)
				.totalHospitals(hospitals)
				.build();
	}
}
