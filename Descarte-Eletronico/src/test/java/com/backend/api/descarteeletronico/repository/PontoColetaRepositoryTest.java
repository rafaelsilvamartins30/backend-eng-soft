package com.backend.api.descarteeletronico.repository;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import com.backend.api.descarteeletronico.model.tipoproduto.TipoProduto;
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

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
class PontoColetaRepositoryTest {

  @Container
  static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:16-alpine")
          .withDatabaseName("descarte_eletronico_test")
          .withUsername("descarte")
          .withPassword("descarte");

  @Autowired private PontoColetaRepository pontoColetaRepository;
  @Autowired private TipoProdutoRepository tipoProdutoRepository;
  @Autowired private JdbcTemplate jdbcTemplate;

  @DynamicPropertySource
  static void configureDatasource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }

  @Test
  void flywayMigrationCreatesPontoColetaTables() {
    Boolean pontoColetaExists =
        jdbcTemplate.queryForObject(
            "select exists (select 1 from information_schema.tables where table_name = 'ponto_coleta')",
            Boolean.class);
    Boolean relationExists =
        jdbcTemplate.queryForObject(
            "select exists (select 1 from information_schema.tables where table_name = 'ponto_coleta_tipo_produto')",
            Boolean.class);

    assertThat(pontoColetaExists).isTrue();
    assertThat(relationExists).isTrue();
  }

  @Test
  void findByIdAndEntityStatusReturnsActiveEntityWithTiposProduto() {
    TipoProduto tipoProduto =
        tipoProdutoRepository.saveAndFlush(
            new TipoProduto("Computadores", "Notebooks, desktops e monitores"));
    PontoColeta saved =
        pontoColetaRepository.saveAndFlush(
            new PontoColeta(
                "EcoPonto Centro",
                "Rua das Flores, 123",
                "Recebe eletrônicos de pequeno porte",
                new BigDecimal("-23.5505200"),
                new BigDecimal("-46.6333080"),
                Set.of(tipoProduto)));

    Optional<PontoColeta> result =
        pontoColetaRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

    assertThat(result).isPresent();
    assertThat(result.get().getNome()).isEqualTo("EcoPonto Centro");
    assertThat(result.get().getTiposProduto()).extracting(TipoProduto::getNome).contains("Computadores");
  }

  @Test
  void findByIdAndEntityStatusIgnoresDeletedEntity() {
    PontoColeta pontoColeta =
        new PontoColeta(
            "EcoPonto Centro",
            "Rua das Flores, 123",
            "Recebe eletrônicos de pequeno porte",
            new BigDecimal("-23.5505200"),
            new BigDecimal("-46.6333080"),
            Set.of());
    pontoColeta.setEntityStatus(EntityStatus.DELETED);
    PontoColeta saved = pontoColetaRepository.saveAndFlush(pontoColeta);

    Optional<PontoColeta> result =
        pontoColetaRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

    assertThat(result).isEmpty();
  }

  @Test
  void findByIdAndEntityStatusIgnoresInactiveEntity() {
    PontoColeta pontoColeta =
        new PontoColeta(
            "EcoPonto Centro",
            "Rua das Flores, 123",
            "Recebe eletrônicos de pequeno porte",
            new BigDecimal("-23.5505200"),
            new BigDecimal("-46.6333080"),
            Set.of());
    pontoColeta.setEntityStatus(EntityStatus.INACTIVE);
    PontoColeta saved = pontoColetaRepository.saveAndFlush(pontoColeta);

    Optional<PontoColeta> result =
        pontoColetaRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

    assertThat(result).isEmpty();
  }

  @Test
  void findAllByEntityStatusReturnsOnlyActiveEntities() {
    PontoColeta active =
        new PontoColeta(
            "EcoPonto Centro",
            "Rua das Flores, 123",
            "Recebe eletrônicos de pequeno porte",
            new BigDecimal("-23.5505200"),
            new BigDecimal("-46.6333080"),
            Set.of());
    PontoColeta deleted =
        new PontoColeta(
            "EcoPonto Bairro",
            "Avenida Brasil, 456",
            "Recebe eletrônicos variados",
            new BigDecimal("-22.9000000"),
            new BigDecimal("-43.2000000"),
            Set.of());
    deleted.setEntityStatus(EntityStatus.DELETED);
    PontoColeta inactive =
        new PontoColeta(
            "EcoPonto Fechado",
            "Avenida Paulista, 789",
            "Temporariamente indisponível",
            new BigDecimal("-23.5600000"),
            new BigDecimal("-46.6500000"),
            Set.of());
    inactive.setEntityStatus(EntityStatus.INACTIVE);
    pontoColetaRepository.saveAllAndFlush(Set.of(active, deleted, inactive));

    Set<PontoColeta> result = pontoColetaRepository.findAllByEntityStatus(EntityStatus.ACTIVE);

    assertThat(result).extracting(PontoColeta::getNome).containsExactly("EcoPonto Centro");
  }
}
