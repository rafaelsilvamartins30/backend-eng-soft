package com.backend.api.descarteeletronico.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import com.backend.api.descarteeletronico.model.tipoproduto.TipoProduto;
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
@DisplayName("PontoColetaRepository - Testes de Persistência")
class PontoColetaRepositoryTest extends BaseRepositoryTest {

  @Autowired private PontoColetaRepository pontoColetaRepository;
  @Autowired private TipoProdutoRepository tipoProdutoRepository;
  @Autowired private JdbcTemplate jdbcTemplate;

  @Nested
  @Order(1)
  @DisplayName("Persistência")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class PersistenceTests {

    @Test
    @Order(1)
    @DisplayName("Deve garantir que a migration do Flyway criou as tabelas de ponto de coleta")
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
  }

  @Nested
  @Order(2)
  @DisplayName("Consultas Customizadas")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class CustomQueryTests {

    @Test
    @Order(1)
    @DisplayName("Deve buscar ponto de coleta ativo por ID com tipos de produto")
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
                  LocalTime.of(8, 0),
                  LocalTime.of(18, 0),
                  Set.of(tipoProduto)));

      Optional<PontoColeta> result =
          pontoColetaRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

      assertThat(result).isPresent();
      assertThat(result.get().getNome()).isEqualTo("EcoPonto Centro");
      assertThat(result.get().getTiposProduto())
          .extracting(TipoProduto::getNome)
          .contains("Computadores");
    }

    @Test
    @Order(2)
    @DisplayName("Deve ignorar ponto de coleta excluído na busca por ID")
    void findByIdAndEntityStatusIgnoresDeletedEntity() {
      PontoColeta pontoColeta =
          new PontoColeta(
              "EcoPonto Centro",
              "Rua das Flores, 123",
              "Recebe eletrônicos de pequeno porte",
              new BigDecimal("-23.5505200"),
              new BigDecimal("-46.6333080"),
              LocalTime.of(8, 0),
              LocalTime.of(18, 0),
              Set.of());
      pontoColeta.setEntityStatus(EntityStatus.DELETED);
      PontoColeta saved = pontoColetaRepository.saveAndFlush(pontoColeta);

      Optional<PontoColeta> result =
          pontoColetaRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

      assertThat(result).isEmpty();
    }

    @Test
    @Order(3)
    @DisplayName("Deve ignorar ponto de coleta inativo na busca por ID")
    void findByIdAndEntityStatusIgnoresInactiveEntity() {
      PontoColeta pontoColeta =
          new PontoColeta(
              "EcoPonto Centro",
              "Rua das Flores, 123",
              "Recebe eletrônicos de pequeno porte",
              new BigDecimal("-23.5505200"),
              new BigDecimal("-46.6333080"),
              LocalTime.of(8, 0),
              LocalTime.of(18, 0),
              Set.of());
      pontoColeta.setEntityStatus(EntityStatus.INACTIVE);
      PontoColeta saved = pontoColetaRepository.saveAndFlush(pontoColeta);

      Optional<PontoColeta> result =
          pontoColetaRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

      assertThat(result).isEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("Deve retornar apenas pontos de coleta ativos na listagem")
    void findAllByEntityStatusReturnsOnlyActiveEntities() {
      pontoColetaRepository.deleteAll();

      PontoColeta active =
          new PontoColeta(
              "EcoPonto Centro",
              "Rua das Flores, 123",
              "Recebe eletrônicos de pequeno porte",
              new BigDecimal("-23.5505200"),
              new BigDecimal("-46.6333080"),
              LocalTime.of(8, 0),
              LocalTime.of(18, 0),
              Set.of());
      PontoColeta deleted =
          new PontoColeta(
              "EcoPonto Bairro",
              "Avenida Brasil, 456",
              "Recebe eletrônicos variados",
              new BigDecimal("-22.9000000"),
              new BigDecimal("-43.2000000"),
              LocalTime.of(8, 0),
              LocalTime.of(18, 0),
              Set.of());
      deleted.setEntityStatus(EntityStatus.DELETED);
      PontoColeta inactive =
          new PontoColeta(
              "EcoPonto Fechado",
              "Avenida Paulista, 789",
              "Temporariamente indisponível",
              new BigDecimal("-23.5600000"),
              new BigDecimal("-46.6500000"),
              LocalTime.of(8, 0),
              LocalTime.of(18, 0),
              Set.of());
      inactive.setEntityStatus(EntityStatus.INACTIVE);
      pontoColetaRepository.saveAllAndFlush(Set.of(active, deleted, inactive));

      Set<PontoColeta> result = pontoColetaRepository.findAllByEntityStatus(EntityStatus.ACTIVE);

      assertThat(result).extracting(PontoColeta::getNome).containsExactly("EcoPonto Centro");
    }
  }
}
