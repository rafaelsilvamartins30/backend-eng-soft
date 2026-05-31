package com.backend.api.descarteeletronico.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

class UsuarioIntegrationTest extends BaseIntegrationTest {

  @Test
  void getMeReturnsAdminDetails() {
    String token = getAdminToken();

    given()
        .header("Authorization", "Bearer " + token)
    .when()
        .get("/api/v1/usuarios/me")
    .then()
        .statusCode(200)
        .body("email", equalTo("admin@descarte.local"))
        .body("nome", is("Administrador"));
  }

  @Test
  void getMeWithoutTokenReturnsUnauthorized() {
    given()
    .when()
        .get("/api/v1/usuarios/me")
    .then()
        .statusCode(401);
  }

  @Test
  void updateMeUpdatesProfile() {
    String token = getAdminToken();

    String updateBody = "{\"nome\": \"Novo Nome\", \"email\": \"novo@descarte.local\"}";

    given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body(updateBody)
    .when()
        .patch("/api/v1/usuarios/me")
    .then()
        .statusCode(200)
        .body("nome", is("Novo Nome"))
        .body("email", is("novo@descarte.local"));
    
    // Reset for other tests if necessary, though each test gets a fresh DB usually
    // but Testcontainers in BaseIntegrationTest is static, so it persists between tests in the same class
    // actually @SpringBootTest with Testcontainers usually shares the DB unless @DirtiesContext
  }
}
