package com.roadguardian.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RoadGuardianBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoadGuardianBackendApplication.class, args);
	}
}
