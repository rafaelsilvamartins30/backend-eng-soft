package com.backend.api.descarteeletronico.integration;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

  @Container
  protected static final PostgreSQLContainer POSTGRES =
      new PostgreSQLContainer("postgres:16-alpine")
          .withDatabaseName("descarte_eletronico_test")
          .withUsername("descarte")
          .withPassword("descarte");

  @LocalServerPort private int port;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
    registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
    registry.add("spring.flyway.user", POSTGRES::getUsername);
    registry.add("spring.flyway.password", POSTGRES::getPassword);
  }

  @BeforeEach
  void setupRestAssured() {
    RestAssured.port = port;
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  protected String getAdminToken() {
    return given()
        .contentType("application/json")
        .body("{\"email\": \"admin@descarte.local\", \"senha\": \"Admin@123\"}")
        .when()
        .post("/api/v1/auth/login")
        .then()
        .statusCode(200)
        .extract()
        .path("accessToken");
  }
}
