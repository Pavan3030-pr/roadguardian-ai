package com.roadguardian.backend.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String password;
	private String role;
	private String vehicleNumber;
}
