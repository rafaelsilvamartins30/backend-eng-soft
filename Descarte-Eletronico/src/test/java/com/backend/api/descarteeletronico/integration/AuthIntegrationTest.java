package com.backend.api.descarteeletronico.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import com.backend.api.descarteeletronico.model.auth.dto.LoginRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

class AuthIntegrationTest extends BaseIntegrationTest {

  @Test
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

  @Test
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
