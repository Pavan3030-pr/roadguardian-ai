package com.roadguardian.backend.model.dto;

import lombok.*;
import com.roadguardian.backend.model.entity.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private User.UserRole role;
	private Boolean active;
	private String vehicleNumber;
	private Double latitude;
	private Double longitude;
}
