package com.roadguardian.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
				.setSubject(email)
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(secretKey, SignatureAlgorithm.HS512)
				.compact();
	}

	public String generateTokenFromEmail(String email) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

		return Jwts.builder()
				.setSubject(email)
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(secretKey, SignatureAlgorithm.HS512)
				.compact();
	}

	public String getEmailFromJWT(String token) {
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody();

		return claims.getSubject();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(secretKey)
					.build()
					.parseClaimsJws(token);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}
