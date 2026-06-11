package com.backend.api.descarteeletronico.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.tipoproduto.TipoProduto;
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
@DisplayName("TipoProdutoRepository - Testes de Persistência")
class TipoProdutoRepositoryTest extends BaseRepositoryTest {

  @Autowired private TipoProdutoRepository tipoProdutoRepository;
  @Autowired private JdbcTemplate jdbcTemplate;

  @Nested
  @Order(1)
  @DisplayName("Persistência")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class PersistenceTests {

    @Test
    @Order(1)
    @DisplayName("Deve garantir que a migration do Flyway criou a tabela tipo_produto")
    void flywayMigrationCreatesTipoProdutoTable() {
      Boolean exists =
          jdbcTemplate.queryForObject(
              "select exists (select 1 from information_schema.tables where table_name = 'tipo_produto')",
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
    @DisplayName("Deve buscar tipo de produto ativo por ID")
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
    @Order(2)
    @DisplayName("Deve ignorar tipo de produto excluído na busca por ID")
    void findByIdAndEntityStatusIgnoresDeletedEntity() {
      TipoProduto tipoProduto = new TipoProduto("Celulares", "Smartphones e carregadores");
      tipoProduto.setEntityStatus(EntityStatus.DELETED);
      TipoProduto saved = tipoProdutoRepository.saveAndFlush(tipoProduto);

      Optional<TipoProduto> result =
          tipoProdutoRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

      assertThat(result).isEmpty();
    }

    @Test
    @Order(3)
    @DisplayName("Deve ignorar tipo de produto inativo na busca por ID")
    void findByIdAndEntityStatusIgnoresInactiveEntity() {
      TipoProduto tipoProduto = new TipoProduto("Celulares", "Smartphones e carregadores");
      tipoProduto.setEntityStatus(EntityStatus.INACTIVE);
      TipoProduto saved = tipoProdutoRepository.saveAndFlush(tipoProduto);

      Optional<TipoProduto> result =
          tipoProdutoRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

      assertThat(result).isEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("Deve retornar apenas tipos de produto ativos filtrados por lista de IDs")
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
              Set.of(active.getId(), savedInactive.getId(), savedDeleted.getId()),
              EntityStatus.ACTIVE);

      assertThat(result).extracting(TipoProduto::getNome).containsExactly("Computadores");
    }
  }
}
