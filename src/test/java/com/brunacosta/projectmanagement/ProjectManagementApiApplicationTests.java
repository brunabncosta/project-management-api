package com.brunacosta.projectmanagement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class ProjectManagementApiApplicationTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres =
			new PostgreSQLContainer<>("postgres:17");

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldCreateEmployeeAndProjectAndListProjectWithEmployee() throws Exception {
		String employeeJson = """
                {
                  "name": "Ana Silva",
                  "cpf": "12345678901",
                  "email": "ana@example.com",
                  "salary": 15000.00
                }
                """;

		mockMvc.perform(post("/api/v1/employees")
						.contentType(MediaType.APPLICATION_JSON)
						.content(employeeJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Ana Silva"));

		String projectJson = """
                {
                  "name": "Project Management API",
                  "employeeIds": [1]
                }
                """;

		mockMvc.perform(post("/api/v1/projects")
						.contentType(MediaType.APPLICATION_JSON)
						.content(projectJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("Project Management API"))
				.andExpect(jsonPath("$.employees[0].name").value("Ana Silva"));

		mockMvc.perform(get("/api/v1/projects"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("Project Management API"))
				.andExpect(jsonPath("$[0].employees[0].name").value("Ana Silva"));
	}
}