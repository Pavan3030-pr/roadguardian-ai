package com.roadguardian.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.util.Date;

@RequiredArgsConstructor
public class JwtTokenProvider {

	private final SecretKey secretKey;
	private final long jwtExpirationMs;

	public String generateToken(Authentication authentication) {
		String email = authentication.getName();
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

		return Jwts.builder()
				.subject(email)
				.issuedAt(now)
				.expiration(expiryDate)
				.signWith(secretKey)
				.compact();
	}

	public String generateTokenFromEmail(String email) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

		return Jwts.builder()
				.subject(email)
				.issuedAt(now)
				.expiration(expiryDate)
				.signWith(secretKey)
				.compact();
	}

	public String getEmailFromJWT(String token) {
		Claims claims = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();

		return claims.getSubject();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser()
					.verifyWith(secretKey)
					.build()
					.parseSignedClaims(token);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}
