package com.backend.api.descarteeletronico.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.enums.TipoRelato;
import com.backend.api.descarteeletronico.model.relato.RelatoProblema;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import java.math.BigDecimal;
import java.time.LocalTime;
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
@DisplayName("RelatoProblemaRepository - Testes de Persistência")
class RelatoProblemaRepositoryTest extends BaseRepositoryTest {

  @Autowired private RelatoProblemaRepository relatoProblemaRepository;
  @Autowired private PontoColetaRepository pontoColetaRepository;
  @Autowired private JdbcTemplate jdbcTemplate;

  @Nested
  @Order(1)
  @DisplayName("Persistência")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class PersistenceTests {

    @Test
    @Order(1)
    @DisplayName("Deve garantir que a migration do Flyway criou a tabela relato_problema")
    void flywayMigrationCreatesRelatoProblemaTable() {
      Boolean tableExists =
              jdbcTemplate.queryForObject(
                      "select exists (select 1 from information_schema.tables where table_name = 'relato_problema')",
                      Boolean.class);

      assertThat(tableExists).isTrue();
    }
  }

  @Nested
  @Order(2)
  @DisplayName("Consultas Customizadas")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class CustomQueryTests {

    @Test
    @Order(1)
    @DisplayName("Deve buscar relatos ativos e inativos por ID, ignorando excluídos")
    void findByIdAndEntityStatusNotReturnsActiveAndInactiveRelatos() {
      RelatoProblema active = relatoProblemaRepository.saveAndFlush(createRelato(EntityStatus.ACTIVE));
      RelatoProblema inactive = relatoProblemaRepository.saveAndFlush(createRelato(EntityStatus.INACTIVE));
      RelatoProblema deleted = relatoProblemaRepository.saveAndFlush(createRelato(EntityStatus.DELETED));

      Optional<RelatoProblema> activeResult =
              relatoProblemaRepository.findByIdAndEntityStatusNot(active.getId(), EntityStatus.DELETED);
      Optional<RelatoProblema> inactiveResult =
              relatoProblemaRepository.findByIdAndEntityStatusNot(inactive.getId(), EntityStatus.DELETED);
      Optional<RelatoProblema> deletedResult =
              relatoProblemaRepository.findByIdAndEntityStatusNot(deleted.getId(), EntityStatus.DELETED);

      assertThat(activeResult).isPresent();
      assertThat(inactiveResult).isPresent();
      assertThat(deletedResult).isEmpty();
    }

    @Test
    @Order(2)
    @DisplayName("Deve retornar todos os relatos não excluídos")
    void findAllByEntityStatusNotIgnoresDeletedRelatos() {
      relatoProblemaRepository.saveAndFlush(createRelato(EntityStatus.ACTIVE));
      relatoProblemaRepository.saveAndFlush(createRelato(EntityStatus.INACTIVE));
      relatoProblemaRepository.saveAndFlush(createRelato(EntityStatus.DELETED));

      Set<RelatoProblema> result = relatoProblemaRepository.findAllByEntityStatusNot(EntityStatus.DELETED);

      assertThat(result).hasSize(2);
      assertThat(result)
              .extracting(RelatoProblema::getEntityStatus)
              .contains(EntityStatus.ACTIVE, EntityStatus.INACTIVE);
    }
  }

  private RelatoProblema createRelato(EntityStatus entityStatus) {
    PontoColeta pontoColeta =
            pontoColetaRepository.saveAndFlush(
                    new PontoColeta(
                            "EcoPonto " + entityStatus,
                            "Rua das Flores, 123",
                            "Recebe eletrônicos",
                            new BigDecimal("-23.5505200"),
                            new BigDecimal("-46.6333080"),
                            LocalTime.of(8, 0),
                            LocalTime.of(18, 0),
                            Set.of()));
    RelatoProblema relato =
            new RelatoProblema(pontoColeta, TipoRelato.LIXEIRA_DANIFICADA, "Maria Silva", "maria@email.com", "Relato de teste");
    relato.setEntityStatus(entityStatus);
    return relato;
  }
}
