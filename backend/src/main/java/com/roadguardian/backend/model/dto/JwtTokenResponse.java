package com.roadguardian.backend.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtTokenResponse {
	private String token;
	private String refreshToken;
	private Long expiresIn;
	private UserDTO user;
}
