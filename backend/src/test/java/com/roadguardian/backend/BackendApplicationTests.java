package com.roadguardian.backend;

import com.roadguardian.backend.controller.AccidentController;
import com.roadguardian.backend.model.Accident;
import com.roadguardian.backend.service.AccidentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BackendApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private AccidentService accidentService;

	@Autowired
	private AccidentController accidentController;

	@Test
	void contextLoads() {
		assertThat(accidentController).isNotNull();
		assertThat(accidentService).isNotNull();
	}

	@Test
	void testGetAllAccidents() {
		var accidents = this.restTemplate.getForObject(
				"http://localhost:" + "8081" + "/api/accidents",
				Accident[].class
		);
		assertThat(accidents).isNotNull();
		assertThat(accidents.length).isGreaterThan(0);
	}

	@Test
	void testGetDashboardMetrics() {
		var metrics = this.restTemplate.getForObject(
				"http://localhost:" + "8081" + "/api/accidents/metrics",
				Object.class
		);
		assertThat(metrics).isNotNull();
	}

}
