package com.backend.api.descarteeletronico.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.backend.api.descarteeletronico.model.pontocoleta.dto.PontoColetaRequest;
import com.backend.api.descarteeletronico.model.relato.dto.RelatoProblemaRequest;
import com.backend.api.descarteeletronico.model.tipoproduto.dto.TipoProdutoRequest;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class ValidationIntegrationTest extends BaseIntegrationTest {

    @Nested
    @Order(1)
    @DisplayName("Validação de Pontos de Coleta")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class PontoColetaValidation {

        @Test
        @Order(1)
        void createPontoWithEmptyFieldsReturnsBadRequest() {
            PontoColetaRequest request = new PontoColetaRequest(
                "", "", "", null, null, null, null, Set.of()
            );

            given()
                .header("Authorization", "Bearer " + getAdminToken())
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post("/api/v1/pontos-coleta")
            .then()
                .statusCode(400)
                .body("message", is("Dados de entrada inválidos"))
                .body("details", hasItems(
                    containsString("nome"),
                    containsString("endereço"),
                    containsString("latitude"),
                    containsString("longitude")
                ));
        }

        @Test
        @Order(2)
        void createPontoWithInvalidCoordinatesReturnsBadRequest() {
            PontoColetaRequest request = new PontoColetaRequest(
                "Ponto", "Rua", "Desc",
                new BigDecimal("91"), new BigDecimal("181"), // Fora dos limites
                null, null, Set.of()
            );

            given()
                .header("Authorization", "Bearer " + getAdminToken())
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post("/api/v1/pontos-coleta")
            .then()
                .statusCode(400)
                .body("details", hasItems(
                    containsString("latitude"),
                    containsString("longitude")
                ));
        }
    }

    @Nested
    @Order(2)
    @DisplayName("Validação de Tipos de Produto")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class TipoProdutoValidation {

        @Test
        @Order(1)
        void createTipoWithEmptyNameReturnsBadRequest() {
            TipoProdutoRequest request = new TipoProdutoRequest("", "Desc");

            given()
                .header("Authorization", "Bearer " + getAdminToken())
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post("/api/v1/tipos-produto")
            .then()
                .statusCode(400)
                .body("details", hasItem(containsString("nome")));
        }
    }

    @Nested
    @Order(3)
    @DisplayName("Validação de Relatos de Problema")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class RelatoValidation {

        @Test
        @Order(1)
        void createRelatoWithInvalidEmailReturnsBadRequest() {
            RelatoProblemaRequest request = new RelatoProblemaRequest(
                null, "User", "email-invalido", "Obs"
            );

            given()
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post("/api/v1/pontos-coleta/" + UUID.randomUUID() + "/relatos-problema")
            .then()
                .statusCode(400)
                .body("details", hasItems(
                    containsString("email"),
                    containsString("tipo")
                ));
        }
    }
}
