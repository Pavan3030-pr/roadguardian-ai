package com.roadguardian.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.roadguardian.backend.model.dto.AccidentDTO;
import com.roadguardian.backend.model.dto.CreateAccidentRequest;
import com.roadguardian.backend.model.entity.Accident;
import com.roadguardian.backend.service.AccidentService;
import com.roadguardian.backend.security.CustomUserDetails;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accidents")
@RequiredArgsConstructor
@Tag(name = "Accidents", description = "Accident management endpoints")
public class AccidentController {

	private final AccidentService accidentService;

	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN', 'POLICE')")
	@Operation(summary = "Report new accident", description = "Create a new accident report")
	public ResponseEntity<AccidentDTO> createAccident(
			@Valid @RequestBody CreateAccidentRequest request,
			Authentication authentication
	) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		return ResponseEntity.ok(accidentService.createAccident(request, userDetails.getUserId()));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get accident details", description = "Fetch accident information by ID")
	public ResponseEntity<AccidentDTO> getAccident(@PathVariable Long id) {
		return ResponseEntity.ok(accidentService.getAccidentById(id));
	}

	@GetMapping
	@Operation(summary = "Get all accidents", description = "Fetch paginated list of accidents")
	public ResponseEntity<List<AccidentDTO>> getAllAccidents(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size
	) {
		return ResponseEntity.ok(accidentService.getAllAccidents(page, size));
	}

	@GetMapping("/severity/{severity}")
	@Operation(summary = "Get accidents by severity", description = "Filter accidents by severity level")
	public ResponseEntity<List<AccidentDTO>> getBySeverity(@PathVariable String severity) {
		return ResponseEntity.ok(accidentService.getAccidentsBySeverity(
				Accident.SeverityLevel.valueOf(severity.toUpperCase())
		));
	}

	@GetMapping("/nearby")
	@Operation(summary = "Find nearby accidents", description = "Get accidents near coordinates")
	public ResponseEntity<List<AccidentDTO>> getNearbyAccidents(
			@RequestParam Double latitude,
			@RequestParam Double longitude,
			@RequestParam(defaultValue = "5") Double radiusKm
	) {
		return ResponseEntity.ok(accidentService.getNearbyAccidents(latitude, longitude, radiusKm));
	}

	@GetMapping("/active/list")
	@Operation(summary = "Get active accidents", description = "Fetch all active (unresolved) accidents")
	public ResponseEntity<List<AccidentDTO>> getActiveAccidents() {
		return ResponseEntity.ok(accidentService.getActiveAccidents());
	}

	@PutMapping("/{id}/status")
	@PreAuthorize("hasAnyRole('ADMIN', 'POLICE', 'AMBULANCE')")
	@Operation(summary = "Update accident status", description = "Change accident status")
	public ResponseEntity<AccidentDTO> updateStatus(
			@PathVariable Long id,
			@RequestParam Accident.IncidentStatus status
	) {
		return ResponseEntity.ok(accidentService.updateAccidentStatus(id, status));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Delete accident", description = "Remove accident record (admin only)")
	public ResponseEntity<Void> deleteAccident(@PathVariable Long id) {
		accidentService.deleteAccident(id);
		return ResponseEntity.ok().build();
	}
}