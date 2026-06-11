package com.backend.api.descarteeletronico.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.usuario.Usuario;
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
@DisplayName("UsuarioRepository - Testes de Persistência")
class UsuarioRepositoryTest extends BaseRepositoryTest {

  @Autowired private UsuarioRepository usuarioRepository;
  @Autowired private JdbcTemplate jdbcTemplate;

  @Nested
  @Order(1)
  @DisplayName("Persistência")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class PersistenceTests {

    @Test
    @Order(1)
    @DisplayName("Deve garantir que a migration do Flyway criou a tabela usuarios_admin")
    void flywayMigrationCreatesUsuariosAdminTable() {
      Boolean exists =
          jdbcTemplate.queryForObject(
              "select exists (select 1 from information_schema.tables where table_name = 'usuarios_admin')",
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
    @DisplayName("Deve buscar usuário ativo por ID")
    void findByIdAndEntityStatusReturnsActiveEntity() {
      Usuario saved =
          usuarioRepository.saveAndFlush(
              new Usuario("Maria Silva", "maria@descarte.com", "SenhaForte123"));

      Optional<Usuario> result =
          usuarioRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

      assertThat(result).isPresent();
      assertThat(result.get().getNome()).isEqualTo("Maria Silva");
      assertThat(result.get().getEntityStatus()).isEqualTo(EntityStatus.ACTIVE);
    }

    @Test
    @Order(2)
    @DisplayName("Deve ignorar usuário excluído na busca por ID")
    void findByIdAndEntityStatusIgnoresDeletedEntity() {
      Usuario usuario = new Usuario("Maria Silva", "maria@descarte.com", "SenhaForte123");
      usuario.setEntityStatus(EntityStatus.DELETED);
      Usuario saved = usuarioRepository.saveAndFlush(usuario);

      Optional<Usuario> result =
          usuarioRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

      assertThat(result).isEmpty();
    }

    @Test
    @Order(3)
    @DisplayName("Deve ignorar usuário inativo na busca por ID")
    void findByIdAndEntityStatusIgnoresInactiveEntity() {
      Usuario usuario = new Usuario("Maria Silva", "maria@descarte.com", "SenhaForte123");
      usuario.setEntityStatus(EntityStatus.INACTIVE);
      Usuario saved = usuarioRepository.saveAndFlush(usuario);

      Optional<Usuario> result =
          usuarioRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

      assertThat(result).isEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("Deve retornar apenas usuários ativos na listagem")
    void findAllByEntityStatusReturnsOnlyActiveEntities() {
      Usuario active = new Usuario("Maria Silva", "maria@descarte.com", "SenhaForte123");
      Usuario deleted = new Usuario("João Souza", "joao@descarte.com", "SenhaForte123");
      deleted.setEntityStatus(EntityStatus.DELETED);
      Usuario inactive = new Usuario("Ana Lima", "ana@descarte.com", "SenhaForte123");
      inactive.setEntityStatus(EntityStatus.INACTIVE);
      usuarioRepository.saveAllAndFlush(Set.of(active, deleted, inactive));

      Set<Usuario> result = usuarioRepository.findAllByEntityStatus(EntityStatus.ACTIVE);

      assertThat(result).extracting(Usuario::getNome).containsExactly("Maria Silva");
    }

    @Test
    @Order(5)
    @DisplayName("Deve verificar existência por e-mail apenas para usuários ativos")
    void existsByEmailAndEntityStatusChecksOnlyActiveEntity() {
      Usuario active = new Usuario("Maria Silva", "maria@descarte.com", "SenhaForte123");
      Usuario deleted = new Usuario("João Souza", "joao@descarte.com", "SenhaForte123");
      deleted.setEntityStatus(EntityStatus.DELETED);
      usuarioRepository.saveAllAndFlush(Set.of(active, deleted));

      assertThat(
              usuarioRepository.existsByEmailAndEntityStatus(
                  "maria@descarte.com", EntityStatus.ACTIVE))
          .isTrue();
      assertThat(
              usuarioRepository.existsByEmailAndEntityStatus(
                  "joao@descarte.com", EntityStatus.ACTIVE))
          .isFalse();
    }
  }
}
