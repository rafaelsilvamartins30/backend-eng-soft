package com.backend.api.descarteeletronico.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.backend.api.descarteeletronico.model.pontocoleta.dto.PontoColetaRequest;
import com.backend.api.descarteeletronico.model.tipoproduto.dto.TipoProdutoRequest;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class PontoColetaIntegrationTest extends BaseIntegrationTest {

  @Nested
  @Order(1)
  @DisplayName("Cenários de Fluxo Completo")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class FluxoCompleto {

    @Test
    @Order(1)
    void fullPontoColetaFlow() {
      String token = getAdminToken();

      // 1. Criar um Tipo de Produto
      TipoProdutoRequest tipoRequest = new TipoProdutoRequest("Eletrônicos", "Celulares, tablets");
      UUID tipoId = given()
          .header("Authorization", "Bearer " + token)
          .contentType(ContentType.JSON)
          .body(tipoRequest)
      .when()
          .post("/api/v1/tipos-produto")
      .then()
          .statusCode(201)
          .extract().jsonPath().getUUID("id");

      // 2. Criar um Ponto de Coleta
      PontoColetaRequest pontoRequest = new PontoColetaRequest(
          "Ponto Teste", "Rua Teste, 100", "Desc",
          new BigDecimal("-23.5"), new BigDecimal("-46.6"),
          LocalTime.of(8, 0), LocalTime.of(18, 0),
          Set.of(tipoId)
      );

      UUID pontoId = given()
          .header("Authorization", "Bearer " + token)
          .contentType(ContentType.JSON)
          .body(pontoRequest)
      .when()
          .post("/api/v1/pontos-coleta")
      .then()
          .statusCode(201)
          .body("nome", is("Ponto Teste"))
          .extract().jsonPath().getUUID("id");

      // 3. Listar pontos (Público)
      given()
      .when()
          .get("/api/v1/pontos-coleta")
      .then()
          .statusCode(200)
          .body("$", hasSize(greaterThanOrEqualTo(1)));

      // 4. Deletar ponto (Admin)
      given()
          .header("Authorization", "Bearer " + token)
      .when()
          .delete("/api/v1/pontos-coleta/" + pontoId)
      .then()
          .statusCode(204);

      // 5. Verificar que sumiu da lista pública
      given()
      .when()
          .get("/api/v1/pontos-coleta/" + pontoId)
      .then()
          .statusCode(404);
    }
  }

  @Nested
  @Order(2)
  @DisplayName("Cenários de Segurança")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Seguranca {

    @Test
    @Order(1)
    void createPontoWithoutTokenReturnsForbidden() {
      PontoColetaRequest pontoRequest = new PontoColetaRequest(
          "Ponto Teste", "Rua Teste, 100", "Desc",
          new BigDecimal("-23.5"), new BigDecimal("-46.6"),
          LocalTime.of(8, 0), LocalTime.of(18, 0),
          Set.of(UUID.randomUUID())
      );

      given()
          .contentType(ContentType.JSON)
          .body(pontoRequest)
      .when()
          .post("/api/v1/pontos-coleta")
      .then()
          .statusCode(401); // Se não enviar nada, o Resource Server retorna 401
    }
  }
}
