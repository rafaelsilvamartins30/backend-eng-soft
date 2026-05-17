package com.backend.api.descarteeletronico.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.usuario.Usuario;
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
class UsuarioRepositoryTest {

  @Container
  static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:16-alpine")
          .withDatabaseName("descarte_eletronico_test")
          .withUsername("descarte")
          .withPassword("descarte");

  @Autowired private UsuarioRepository usuarioRepository;
  @Autowired private JdbcTemplate jdbcTemplate;

  @DynamicPropertySource
  static void configureDatasource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }

  @Test
  void flywayMigrationCreatesUsuariosAdminTable() {
    Boolean exists =
        jdbcTemplate.queryForObject(
            "select exists (select 1 from information_schema.tables where table_name = 'usuarios_admin')",
            Boolean.class);

    assertThat(exists).isTrue();
  }

  @Test
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
  void findByIdAndEntityStatusIgnoresDeletedEntity() {
    Usuario usuario = new Usuario("Maria Silva", "maria@descarte.com", "SenhaForte123");
    usuario.setEntityStatus(EntityStatus.DELETED);
    Usuario saved = usuarioRepository.saveAndFlush(usuario);

    Optional<Usuario> result =
        usuarioRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

    assertThat(result).isEmpty();
  }

  @Test
  void findByIdAndEntityStatusIgnoresInactiveEntity() {
    Usuario usuario = new Usuario("Maria Silva", "maria@descarte.com", "SenhaForte123");
    usuario.setEntityStatus(EntityStatus.INACTIVE);
    Usuario saved = usuarioRepository.saveAndFlush(usuario);

    Optional<Usuario> result =
        usuarioRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

    assertThat(result).isEmpty();
  }

  @Test
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
