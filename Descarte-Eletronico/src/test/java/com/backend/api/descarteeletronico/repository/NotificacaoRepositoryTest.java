package com.backend.api.descarteeletronico.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.enums.TipoRelato;
import com.backend.api.descarteeletronico.model.relato.RelatoProblema;
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
  void findAllByRelatoProblemaIdAndEntityStatusReturnsOnlyActiveNotifications() {
    PontoColeta pontoColeta = savePontoColeta("EcoPonto Centro");
    RelatoProblema relato =
            relatoProblemaRepository.saveAndFlush(
                    new RelatoProblema(pontoColeta, TipoRelato.LIXEIRA_CHEIA, "Maria Silva", "maria@email.com", "Relato de teste"));

    Notificacao active = new Notificacao("Lixeira Cheia", "Novo relato recebido.", pontoColeta, relato);
    Notificacao inactive = new Notificacao("Lixeira Cheia", "Novo relato recebido.", pontoColeta, relato);
    inactive.setEntityStatus(EntityStatus.INACTIVE);

    notificacaoRepository.saveAllAndFlush(Set.of(active, inactive));

    Set<Notificacao> result =
            notificacaoRepository.findAllByRelatoProblemaIdAndEntityStatus(
                    relato.getId(), EntityStatus.ACTIVE);

    assertThat(result).hasSize(1);
    assertThat(result)
            .extracting(Notificacao::getEntityStatus)
            .containsExactly(EntityStatus.ACTIVE);
  }

  private Notificacao createNotificacao(EntityStatus entityStatus) {
    PontoColeta pontoColeta = savePontoColeta("EcoPonto " + entityStatus);
    RelatoProblema relato = relatoProblemaRepository.saveAndFlush(
            new RelatoProblema(pontoColeta, TipoRelato.LIXEIRA_CHEIA, "Maria", "m@m.com", "Cheio"));

    Notificacao notificacao = new Notificacao("Lixeira Cheia", "Ponto reportado como cheio.", pontoColeta, relato);
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