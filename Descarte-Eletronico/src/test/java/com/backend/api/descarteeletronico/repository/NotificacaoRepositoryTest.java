package com.backend.api.descarteeletronico.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.enums.NotificacaoTipo;
import com.backend.api.descarteeletronico.model.feedback.Feedback;
import com.backend.api.descarteeletronico.model.notificacao.Notificacao;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import java.math.BigDecimal;
import java.time.LocalTime;
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
class NotificacaoRepositoryTest {

  @Container
  static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:16-alpine")
          .withDatabaseName("descarte_eletronico_test")
          .withUsername("descarte")
          .withPassword("descarte");

  @Autowired private NotificacaoRepository notificacaoRepository;
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
  void flywayMigrationCreatesNotificacaoTable() {
    Boolean notificacaoExists =
        jdbcTemplate.queryForObject(
            "select exists (select 1 from information_schema.tables where table_name = 'notificacao')",
            Boolean.class);

    assertThat(notificacaoExists).isTrue();
  }

  @Test
  void findAllByEntityStatusReturnsOnlyUnreadNotifications() {
    notificacaoRepository.saveAndFlush(createNotificacao(EntityStatus.ACTIVE));
    notificacaoRepository.saveAndFlush(createNotificacao(EntityStatus.INACTIVE));
    notificacaoRepository.saveAndFlush(createNotificacao(EntityStatus.DELETED));

    Set<Notificacao> result = notificacaoRepository.findAllByEntityStatus(EntityStatus.ACTIVE);

    assertThat(result).hasSize(1);
    assertThat(result)
        .extracting(Notificacao::getEntityStatus)
        .containsExactly(EntityStatus.ACTIVE);
  }

  @Test
  void findAllByFeedbackIdAndEntityStatusReturnsOnlyActiveFeedbackNotifications() {
    PontoColeta pontoColeta = savePontoColeta("EcoPonto Centro");
    Feedback feedback =
        feedbackRepository.saveAndFlush(
            new Feedback(pontoColeta, "Maria Silva", "maria@email.com", "Feedback de teste"));
    Notificacao active =
        new Notificacao(
            NotificacaoTipo.FEEDBACK_RECEBIDO,
            "Feedback recebido",
            "Novo feedback recebido.",
            pontoColeta,
            feedback);
    Notificacao inactive =
        new Notificacao(
            NotificacaoTipo.FEEDBACK_RECEBIDO,
            "Feedback recebido",
            "Novo feedback recebido.",
            pontoColeta,
            feedback);
    inactive.setEntityStatus(EntityStatus.INACTIVE);
    notificacaoRepository.saveAllAndFlush(Set.of(active, inactive));

    Set<Notificacao> result =
        notificacaoRepository.findAllByFeedbackIdAndEntityStatus(
            feedback.getId(), EntityStatus.ACTIVE);

    assertThat(result).hasSize(1);
    assertThat(result)
        .extracting(Notificacao::getEntityStatus)
        .containsExactly(EntityStatus.ACTIVE);
  }

  private Notificacao createNotificacao(EntityStatus entityStatus) {
    PontoColeta pontoColeta = savePontoColeta("EcoPonto " + entityStatus);
    Notificacao notificacao =
        new Notificacao(
            NotificacaoTipo.PONTO_COLETA_CHEIO,
            "Ponto de coleta cheio",
            "Ponto reportado como cheio.",
            pontoColeta,
            null);
    notificacao.setEntityStatus(entityStatus);
    return notificacao;
  }

  private PontoColeta savePontoColeta(String nome) {
    return pontoColetaRepository.saveAndFlush(
        new PontoColeta(
            nome,
            "Rua das Flores, 123",
            "Recebe eletrônicos",
            new BigDecimal("-23.5505200"),
            new BigDecimal("-46.6333080"),
            LocalTime.of(8, 0),
            LocalTime.of(18, 0),
            Set.of()));
  }
}
