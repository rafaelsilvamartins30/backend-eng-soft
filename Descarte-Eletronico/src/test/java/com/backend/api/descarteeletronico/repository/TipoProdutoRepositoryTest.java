package com.backend.api.descarteeletronico.repository;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
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

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
class TipoProdutoRepositoryTest {

  @Container
  static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:16-alpine")
          .withDatabaseName("descarte_eletronico_test")
          .withUsername("descarte")
          .withPassword("descarte");

  @Autowired private TipoProdutoRepository tipoProdutoRepository;
  @Autowired private JdbcTemplate jdbcTemplate;

  @DynamicPropertySource
  static void configureDatasource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }

  @Test
  void flywayMigrationCreatesTipoProdutoTable() {
    Boolean exists =
        jdbcTemplate.queryForObject(
            "select exists (select 1 from information_schema.tables where table_name = 'tipo_produto')",
            Boolean.class);

    assertThat(exists).isTrue();
  }

  @Test
  void findByIdAndEntityStatusReturnsActiveEntity() {
    TipoProduto saved =
        tipoProdutoRepository.saveAndFlush(
            new TipoProduto("Computadores", "Notebooks, desktops e monitores"));

    Optional<TipoProduto> result =
        tipoProdutoRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

    assertThat(result).isPresent();
    assertThat(result.get().getNome()).isEqualTo("Computadores");
    assertThat(result.get().getEntityStatus()).isEqualTo(EntityStatus.ACTIVE);
  }

  @Test
  void findByIdAndEntityStatusIgnoresDeletedEntity() {
    TipoProduto tipoProduto = new TipoProduto("Celulares", "Smartphones e carregadores");
    tipoProduto.setEntityStatus(EntityStatus.DELETED);
    TipoProduto saved = tipoProdutoRepository.saveAndFlush(tipoProduto);

    Optional<TipoProduto> result =
        tipoProdutoRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

    assertThat(result).isEmpty();
  }

  @Test
  void findByIdAndEntityStatusIgnoresInactiveEntity() {
    TipoProduto tipoProduto = new TipoProduto("Celulares", "Smartphones e carregadores");
    tipoProduto.setEntityStatus(EntityStatus.INACTIVE);
    TipoProduto saved = tipoProdutoRepository.saveAndFlush(tipoProduto);

    Optional<TipoProduto> result =
        tipoProdutoRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

    assertThat(result).isEmpty();
  }

  @Test
  void findAllByIdInAndEntityStatusReturnsOnlyActiveEntities() {
    TipoProduto active =
        tipoProdutoRepository.save(new TipoProduto("Computadores", "Notebooks e desktops"));
    TipoProduto inactive = new TipoProduto("Pilhas", "Pilhas e baterias");
    inactive.setEntityStatus(EntityStatus.INACTIVE);
    TipoProduto savedInactive = tipoProdutoRepository.save(inactive);
    TipoProduto deleted = new TipoProduto("Celulares", "Smartphones");
    deleted.setEntityStatus(EntityStatus.DELETED);
    TipoProduto savedDeleted = tipoProdutoRepository.saveAndFlush(deleted);

    Set<TipoProduto> result =
        tipoProdutoRepository.findAllByIdInAndEntityStatus(
            Set.of(active.getId(), savedInactive.getId(), savedDeleted.getId()), EntityStatus.ACTIVE);

    assertThat(result).extracting(TipoProduto::getNome).containsExactly("Computadores");
  }
}
