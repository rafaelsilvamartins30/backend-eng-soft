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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
class RelatoProblemaRepositoryTest {

  @Container
  static final PostgreSQLContainer POSTGRES =
      new PostgreSQLContainer("postgres:16-alpine")
          .withDatabaseName("descarte_eletronico_test")
          .withUsername("descarte")
          .withPassword("descarte");

  @Autowired private RelatoProblemaRepository relatoProblemaRepository;
  @Autowired private PontoColetaRepository pontoColetaRepository;
  @Autowired private JdbcTemplate jdbcTemplate;

  @DynamicPropertySource
  static void configureDatasource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }

  @Test
  void flywayMigrationCreatesRelatoProblemaTable() {
    Boolean tableExists =
            jdbcTemplate.queryForObject(
                    "select exists (select 1 from information_schema.tables where table_name = 'relato_problema')",
                    Boolean.class);

    assertThat(tableExists).isTrue();
  }

  @Test
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