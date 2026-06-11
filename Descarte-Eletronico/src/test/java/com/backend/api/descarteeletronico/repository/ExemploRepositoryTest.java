package com.backend.api.descarteeletronico.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.exemplo.Exemplo;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

@DataJpaTest
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("ExemploRepository - Testes de Persistência")
class ExemploRepositoryTest extends BaseRepositoryTest {

  @Autowired private ExemploRepository exemploRepository;
  @Autowired private JdbcTemplate jdbcTemplate;

  @Nested
  @Order(1)
  @DisplayName("Persistência")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class PersistenceTests {

    @Test
    @Order(1)
    @DisplayName("Deve garantir que a migration do Flyway criou a tabela exemplo")
    void flywayMigrationCreatesExemploTable() {
      Boolean exists =
          jdbcTemplate.queryForObject(
              "select exists (select 1 from information_schema.tables where table_name = 'exemplo')",
              Boolean.class);

      assertThat(exists).isTrue();
    }
  }

  @Nested
  @Order(2)
  @DisplayName("Consultas Customizadas")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class CustomQueryTests {

    @Test
    @Order(1)
    @DisplayName("Deve buscar entidade ativa por ID")
    void findByIdAndEntityStatusReturnsActiveEntity() {
      Exemplo saved = exemploRepository.saveAndFlush(new Exemplo("Coleta", "Notebook antigo"));

      Optional<Exemplo> result =
          exemploRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

      assertThat(result).isPresent();
      assertThat(result.get().getNome()).isEqualTo("Coleta");
      assertThat(result.get().getEntityStatus()).isEqualTo(EntityStatus.ACTIVE);
    }

    @Test
    @Order(2)
    @DisplayName("Deve ignorar entidade excluída na busca por ID")
    void findByIdAndEntityStatusIgnoresDeletedEntity() {
      Exemplo exemplo = new Exemplo("Coleta", "Notebook antigo");
      exemplo.setEntityStatus(EntityStatus.DELETED);
      Exemplo saved = exemploRepository.saveAndFlush(exemplo);

      Optional<Exemplo> result =
          exemploRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

      assertThat(result).isEmpty();
    }

    @Test
    @Order(3)
    @DisplayName("Deve ignorar entidade inativa na busca por ID")
    void findByIdAndEntityStatusIgnoresInactiveEntity() {
      Exemplo exemplo = new Exemplo("Coleta", "Notebook antigo");
      exemplo.setEntityStatus(EntityStatus.INACTIVE);
      Exemplo saved = exemploRepository.saveAndFlush(exemplo);

      Optional<Exemplo> result =
          exemploRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

      assertThat(result).isEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("Deve retornar apenas entidades ativas na listagem")
    void findAllByEntityStatusReturnsOnlyActiveEntities() {
      Exemplo active = new Exemplo("Ativo", "Registro visível");
      Exemplo inactive = new Exemplo("Inativo", "Registro indisponível");
      inactive.setEntityStatus(EntityStatus.INACTIVE);
      Exemplo deleted = new Exemplo("Deletado", "Registro removido");
      deleted.setEntityStatus(EntityStatus.DELETED);
      exemploRepository.saveAllAndFlush(Set.of(active, inactive, deleted));

      Set<Exemplo> result = exemploRepository.findAllByEntityStatus(EntityStatus.ACTIVE);

      assertThat(result).extracting(Exemplo::getNome).containsExactly("Ativo");
    }
  }
}
