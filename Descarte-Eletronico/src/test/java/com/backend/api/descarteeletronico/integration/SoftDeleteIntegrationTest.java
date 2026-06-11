package com.backend.api.descarteeletronico.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.exemplo.dto.ExemploRequest;
import com.backend.api.descarteeletronico.repository.ExemploRepository;
import io.restassured.http.ContentType;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class SoftDeleteIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ExemploRepository exemploRepository;

    @Test
    @DisplayName("Garantir que a remoção via API executa Logicamente (Soft Delete)")
    void deleteExecutesSoftDeleteInDatabase() {
        String token = getAdminToken();

        // 1. Criar um registro via API
        ExemploRequest request = new ExemploRequest("Para Deletar", "Descrição");
        UUID id = given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/exemplos")
        .then()
            .statusCode(201)
            .extract().jsonPath().getUUID("id");

        // 2. Deletar via API
        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .delete("/api/v1/exemplos/" + id)
        .then()
            .statusCode(204);

        // 3. Verificar no Banco de Dados (Repository ignore EntityStatus filter)
        // Usamos o repository diretamente para checar o estado real da linha
        var entityOpt = exemploRepository.findById(id);
        
        assertThat(entityOpt).isPresent();
        assertThat(entityOpt.get().getEntityStatus()).isEqualTo(EntityStatus.DELETED);
        assertThat(entityOpt.get().getDeletedAt()).isNotNull();

        // 4. Garantir que a API não o encontra mais em buscas normais
        given()
        .when()
            .get("/api/v1/exemplos/" + id)
        .then()
            .statusCode(404);
    }
}
