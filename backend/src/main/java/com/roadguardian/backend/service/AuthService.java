package com.roadguardian.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roadguardian.backend.exception.InvalidRequestException;
import com.roadguardian.backend.exception.ResourceNotFoundException;
import com.roadguardian.backend.model.dto.request.LoginRequest;
import com.roadguardian.backend.model.dto.request.RegisterRequest;
import com.roadguardian.backend.model.dto.response.JwtAuthResponse;
import com.roadguardian.backend.model.dto.response.UserResponse;
import com.roadguardian.backend.model.entity.User;
import com.roadguardian.backend.model.entity.Role;
import com.roadguardian.backend.model.entity.RefreshToken;
import com.roadguardian.backend.repository.UserRepository;
import com.roadguardian.backend.repository.RoleRepository;
import com.roadguardian.backend.repository.RefreshTokenRepository;
import com.roadguardian.backend.security.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final PasswordEncoder passwordEncoder;

	@Value("${app.jwt.expiration:86400000}")
	private Long jwtExpiration;

	@Value("${app.jwt.refresh-expiration:604800000}")
	private Long refreshTokenExpiration;

	public JwtAuthResponse login(LoginRequest request) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							request.getEmail(),
							request.getPassword()
					)
			);

			String accessToken = jwtTokenProvider.generateToken(authentication);
			User user = userRepository.findByEmail(request.getEmail())
					.orElseThrow(() -> new ResourceNotFoundException("User not found"));

			RefreshToken refreshToken = createRefreshToken(user);
			user.setLastLoginAt(LocalDateTime.now());
			userRepository.save(user);

			log.info("User {} logged in successfully", request.getEmail());

			return JwtAuthResponse.builder()
					.accessToken(accessToken)
					.refreshToken(refreshToken.getToken())
					.tokenType("Bearer")
					.expiresIn(jwtExpiration)
					.user(convertToUserResponse(user))
					.build();
		} catch (Exception ex) {
			log.error("Login failed for email: {}", request.getEmail(), ex);
			throw new InvalidRequestException("Invalid email or password");
		}
	}

	public JwtAuthResponse register(RegisterRequest request) {
		// Validate input
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new InvalidRequestException("Email already registered");
		}

		if (userRepository.existsByPhone(request.getPhone())) {
			throw new InvalidRequestException("Phone number already registered");
		}

		// Get role
		Role role = roleRepository.findByName(request.getRole())
				.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.getRole()));

		// Create user
		User user = User.builder()
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.email(request.getEmail())
				.phone(request.getPhone())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(role)
				.active(true)
				.emailVerified(false)
				.build();

		user = userRepository.save(user);

		// Generate tokens
		String accessToken = jwtTokenProvider.generateTokenFromEmail(user.getEmail());
		RefreshToken refreshToken = createRefreshToken(user);

		user.setLastLoginAt(LocalDateTime.now());
		userRepository.save(user);

		log.info("New user registered: {}", request.getEmail());

		return JwtAuthResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken.getToken())
				.tokenType("Bearer")
				.expiresIn(jwtExpiration)
				.user(convertToUserResponse(user))
				.build();
	}

	public JwtAuthResponse refreshToken(String refreshToken) {
		RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
				.orElseThrow(() -> new InvalidRequestException("Invalid refresh token"));

		if (!token.isValid()) {
			throw new InvalidRequestException("Refresh token expired or revoked");
		}

		User user = token.getUser();
		String newAccessToken = jwtTokenProvider.generateTokenFromEmail(user.getEmail());

		log.info("Access token refreshed for user: {}", user.getEmail());

		return JwtAuthResponse.builder()
				.accessToken(newAccessToken)
				.refreshToken(refreshToken)
				.tokenType("Bearer")
				.expiresIn(jwtExpiration)
				.user(convertToUserResponse(user))
				.build();
	}

	public void logout(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		RefreshToken token = refreshTokenRepository.findByUser(user).orElse(null);
		if (token != null) {
			token.setRevoked(true);
			token.setRevokedAt(LocalDateTime.now());
			refreshTokenRepository.save(token);
		}

		log.info("User {} logged out", user.getEmail());
	}

	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new InvalidRequestException("User not authenticated");
		}

		String email = authentication.getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
	}

	private RefreshToken createRefreshToken(User user) {
		// Revoke existing tokens
		refreshTokenRepository.findByUser(user).ifPresent(token -> {
			token.setRevoked(true);
			refreshTokenRepository.save(token);
		});

		// Create new token
		RefreshToken refreshToken = RefreshToken.builder()
				.token(UUID.randomUUID().toString())
				.user(user)
				.expiresAt(LocalDateTime.now().plusNanos(refreshTokenExpiration * 1_000_000))
				.revoked(false)
				.build();

		return refreshTokenRepository.save(refreshToken);
	}

	private UserResponse convertToUserResponse(User user) {
		return UserResponse.builder()
				.id(user.getId())
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.email(user.getEmail())
				.phone(user.getPhone())
				.role(user.getRole().getName())
				.active(user.getActive())
				.emailVerified(user.getEmailVerified())
				.profileImageUrl(user.getProfileImageUrl())
				.createdAt(user.getCreatedAt())
				.updatedAt(user.getUpdatedAt())
				.build();
	}
}
