package com.backend.api.descarteeletronico.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.backend.api.descarteeletronico.model.enums.TipoRelato;
import com.backend.api.descarteeletronico.model.pontocoleta.dto.PontoColetaRequest;
import com.backend.api.descarteeletronico.model.relato.dto.RelatoProblemaRequest;
import com.backend.api.descarteeletronico.model.tipoproduto.dto.TipoProdutoRequest;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FeedbackIntegrationTest extends BaseIntegrationTest {

  @Test
  void relatoFlowCreatesNotification() {
    String token = getAdminToken();

    // 1. Setup: Criar Tipo e Ponto
    TipoProdutoRequest tipoRequest = new TipoProdutoRequest("Pilhas", "AAA, AA");
    UUID tipoId = given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body(tipoRequest)
    .when()
        .post("/api/v1/tipos-produto")
    .then()
        .statusCode(201)
        .extract().jsonPath().getUUID("id");

    PontoColetaRequest pontoRequest = new PontoColetaRequest(
        "Ponto Alerta", "Rua B", "Desc",
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
        .extract().jsonPath().getUUID("id");

    // 2. Criar Relato (Público)
    RelatoProblemaRequest relatoRequest = new RelatoProblemaRequest(
        TipoRelato.LIXEIRA_CHEIA, "Carlos", "carlos@test.com", "Lixeira transbordando"
    );

    given()
        .contentType(ContentType.JSON)
        .body(relatoRequest)
    .when()
        .post("/api/v1/pontos-coleta/" + pontoId + "/relatos-problema")
    .then()
        .statusCode(201)
        .body("nome", equalTo("Carlos"))
        .body("tipoRelato", equalTo("LIXEIRA_CHEIA"));

    // 3. Verificar Notificações (Admin)
    given()
        .header("Authorization", "Bearer " + token)
    .when()
        .get("/api/v1/notificacoes")
    .then()
        .statusCode(200)
        .body("$", hasSize(greaterThanOrEqualTo(1)))
        .body("[0].titulo", equalTo("Lixeira Cheia"))
        .body("[0].pontoColetaNome", equalTo("Ponto Alerta"));

    // 4. Marcar como vista
    UUID notificacaoId = given()
        .header("Authorization", "Bearer " + token)
    .when()
        .get("/api/v1/notificacoes")
    .then()
        .extract().jsonPath().getUUID("[0].id");

    given()
        .header("Authorization", "Bearer " + token)
    .when()
        .patch("/api/v1/notificacoes/" + notificacaoId + "/visualizar")
    .then()
        .statusCode(200)
        .body("entityStatus", equalTo("INACTIVE"));
  }
}
