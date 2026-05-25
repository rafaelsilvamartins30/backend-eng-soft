package com.backend.api.descarteeletronico.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.feedback.Feedback;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
class FeedbackRepositoryTest {

  @Container
  static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:16-alpine")
          .withDatabaseName("descarte_eletronico_test")
          .withUsername("descarte")
          .withPassword("descarte");

  @Autowired private FeedbackRepository feedbackRepository;
  @Autowired private PontoColetaRepository pontoColetaRepository;
  @Autowired private JdbcTemplate jdbcTemplate;

  @DynamicPropertySource
  static void configureDatasource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }

  @Test
  void flywayMigrationCreatesFeedbackTable() {
    Boolean feedbackExists =
        jdbcTemplate.queryForObject(
            "select exists (select 1 from information_schema.tables where table_name = 'feedback')",
            Boolean.class);

    assertThat(feedbackExists).isTrue();
  }

  @Test
  void findByIdAndEntityStatusNotReturnsActiveAndInactiveFeedbacks() {
    Feedback active = feedbackRepository.saveAndFlush(createFeedback(EntityStatus.ACTIVE));
    Feedback inactive = feedbackRepository.saveAndFlush(createFeedback(EntityStatus.INACTIVE));
    Feedback deleted = feedbackRepository.saveAndFlush(createFeedback(EntityStatus.DELETED));

    Optional<Feedback> activeResult =
        feedbackRepository.findByIdAndEntityStatusNot(active.getId(), EntityStatus.DELETED);
    Optional<Feedback> inactiveResult =
        feedbackRepository.findByIdAndEntityStatusNot(inactive.getId(), EntityStatus.DELETED);
    Optional<Feedback> deletedResult =
        feedbackRepository.findByIdAndEntityStatusNot(deleted.getId(), EntityStatus.DELETED);

    assertThat(activeResult).isPresent();
    assertThat(inactiveResult).isPresent();
    assertThat(deletedResult).isEmpty();
  }

  @Test
  void findAllByEntityStatusNotIgnoresDeletedFeedbacks() {
    feedbackRepository.saveAndFlush(createFeedback(EntityStatus.ACTIVE));
    feedbackRepository.saveAndFlush(createFeedback(EntityStatus.INACTIVE));
    feedbackRepository.saveAndFlush(createFeedback(EntityStatus.DELETED));

    Set<Feedback> result = feedbackRepository.findAllByEntityStatusNot(EntityStatus.DELETED);

    assertThat(result).hasSize(2);
    assertThat(result)
        .extracting(Feedback::getEntityStatus)
        .contains(EntityStatus.ACTIVE, EntityStatus.INACTIVE);
  }

  private Feedback createFeedback(EntityStatus entityStatus) {
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
    Feedback feedback =
        new Feedback(pontoColeta, "Maria Silva", "maria@email.com", "Feedback de teste");
    feedback.setEntityStatus(entityStatus);
    return feedback;
  }
}
