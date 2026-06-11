package com.backend.api.descarteeletronico.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import io.restassured.http.ContentType;
import java.util.UUID;
import org.junit.jupiter.api.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class SecurityIntegrationTest extends BaseIntegrationTest {

    @Nested
    @Order(1)
    @DisplayName("Proteção de Endpoints Administrativos")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class AdminProtection {

        @Test
        @Order(1)
        void accessAdminEndpointWithoutTokenReturnsUnauthorized() {
            given()
            .when()
                .get("/api/v1/notificacoes")
            .then()
                .statusCode(401);
        }

        @Test
        @Order(2)
        void accessAdminEndpointWithInvalidTokenReturnsUnauthorized() {
            given()
                .header("Authorization", "Bearer token-invalido")
            .when()
                .get("/api/v1/notificacoes")
            .then()
                .statusCode(401);
        }

        @Test
        @Order(3)
        void postAdminEndpointWithoutTokenReturnsUnauthorized() {
            given()
                .contentType(ContentType.JSON)
                .body("{}")
            .when()
                .post("/api/v1/pontos-coleta")
            .then()
                .statusCode(401);
        }
    }

    @Nested
    @Order(2)
    @DisplayName("Permissões de Endpoints Públicos")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class PublicAccess {

        @Test
        @Order(1)
        void listPontosColetaIsPublic() {
            given()
            .when()
                .get("/api/v1/pontos-coleta")
            .then()
                .statusCode(200);
        }

        @Test
        @Order(2)
        void listTiposProdutoIsPublic() {
            given()
            .when()
                .get("/api/v1/tipos-produto")
            .then()
                .statusCode(200);
        }

        @Test
        @Order(3)
        void getHealthIsPublic() {
            given()
            .when()
                .get("/health")
            .then()
                .statusCode(200)
                .body("status", is("UP"));
        }
    }
}
