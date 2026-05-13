package com.roadguardian.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roadguardian.backend.exception.InvalidRequestException;
import com.roadguardian.backend.exception.ResourceNotFoundException;
import com.roadguardian.backend.model.dto.JwtTokenResponse;
import com.roadguardian.backend.model.dto.LoginRequest;
import com.roadguardian.backend.model.dto.RegisterRequest;
import com.roadguardian.backend.model.dto.UserDTO;
import com.roadguardian.backend.model.entity.User;
import com.roadguardian.backend.repository.UserRepository;
import com.roadguardian.backend.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

	private final UserRepository userRepository;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final PasswordEncoder passwordEncoder;

	public JwtTokenResponse login(LoginRequest request) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							request.getEmail(),
							request.getPassword()
					)
			);

			String token = jwtTokenProvider.generateToken(authentication);
			User user = userRepository.findByEmail(request.getEmail())
					.orElseThrow(() -> new ResourceNotFoundException("User not found"));

			return JwtTokenResponse.builder()
					.token(token)
					.expiresIn(86400000L) // 24 hours
					.user(convertToDTO(user))
					.build();
		} catch (Exception ex) {
			log.error("Login failed for email: {}", request.getEmail());
			throw new InvalidRequestException("Invalid email or password");
		}
	}

	public JwtTokenResponse register(RegisterRequest request) {
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new InvalidRequestException("Email already registered");
		}

		User user = User.builder()
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.email(request.getEmail())
				.phone(request.getPhone())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(User.UserRole.valueOf(request.getRole().toUpperCase()))
				.vehicleNumber(request.getVehicleNumber())
				.active(true)
				.build();

		user = userRepository.save(user);
		String token = jwtTokenProvider.generateTokenFromEmail(user.getEmail());

		return JwtTokenResponse.builder()
				.token(token)
				.expiresIn(86400000L)
				.user(convertToDTO(user))
				.build();
	}

	public UserDTO getUserById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
		return convertToDTO(user);
	}

	private UserDTO convertToDTO(User user) {
		return UserDTO.builder()
				.id(user.getId())
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.email(user.getEmail())
				.phone(user.getPhone())
				.role(user.getRole())
				.active(user.getActive())
				.vehicleNumber(user.getVehicleNumber())
				.latitude(user.getLatitude())
				.longitude(user.getLongitude())
				.build();
	}
}
