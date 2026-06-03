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
    
    // Restore admin to avoid test pollution
    String newToken = given()
        .contentType("application/json")
        .body("{\"email\": \"novo@descarte.local\", \"senha\": \"Admin@123\"}")
        .when()
        .post("/api/v1/auth/login")
        .then()
        .statusCode(200)
        .extract()
        .path("accessToken");
        
    String revertBody = "{\"nome\": \"Administrador\", \"email\": \"admin@descarte.local\"}";
    given()
        .header("Authorization", "Bearer " + newToken)
        .contentType(ContentType.JSON)
        .body(revertBody)
    .when()
        .patch("/api/v1/usuarios/me")
    .then()
        .statusCode(200);
  }
}
