package com.backend.api.descarteeletronico.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import com.backend.api.descarteeletronico.model.auth.dto.LoginRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class AuthIntegrationTest extends BaseIntegrationTest {

  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class FluxosDeSucesso {

    @Test
    @Order(1)
    void loginWithDefaultAdminReturnsToken() {
      LoginRequest loginRequest = new LoginRequest("admin@descarte.local", "Admin@123");

      given()
          .contentType(ContentType.JSON)
          .body(loginRequest)
      .when()
          .post("/api/v1/auth/login")
      .then()
          .statusCode(200)
          .body("accessToken", notNullValue());
    }
  }

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class CenariosDeErroSeguranca {

    @Test
    @Order(1)
    void loginWithInvalidCredentialsReturnsUnauthorized() {
      LoginRequest loginRequest = new LoginRequest("wrong@email.com", "wrongpass");

      given()
          .contentType(ContentType.JSON)
          .body(loginRequest)
      .when()
          .post("/api/v1/auth/login")
      .then()
          .statusCode(401);
    }
  }
}
