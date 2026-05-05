package com.backend.api.descarteeletronico.repository;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.exemplo.Exemplo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
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
class ExemploRepositoryTest {

  @Container
  static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:16-alpine")
          .withDatabaseName("descarte_eletronico_test")
          .withUsername("descarte")
          .withPassword("descarte");

  @Autowired private ExemploRepository exemploRepository;

  @DynamicPropertySource
  static void configureDatasource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }

  @Test
  void findByIdAndEntityStatusNotReturnsActiveEntity() {
    Exemplo saved = exemploRepository.saveAndFlush(new Exemplo("Coleta", "Notebook antigo"));

    Optional<Exemplo> result =
        exemploRepository.findByIdAndEntityStatusNot(saved.getId(), EntityStatus.DELETED);

    assertThat(result).isPresent();
    assertThat(result.get().getNome()).isEqualTo("Coleta");
    assertThat(result.get().getEntityStatus()).isEqualTo(EntityStatus.ACTIVE);
  }

  @Test
  void findByIdAndEntityStatusNotIgnoresDeletedEntity() {
    Exemplo exemplo = new Exemplo("Coleta", "Notebook antigo");
    exemplo.setEntityStatus(EntityStatus.DELETED);
    Exemplo saved = exemploRepository.saveAndFlush(exemplo);

    Optional<Exemplo> result =
        exemploRepository.findByIdAndEntityStatusNot(saved.getId(), EntityStatus.DELETED);

    assertThat(result).isEmpty();
  }

  @Test
  void findAllByEntityStatusNotReturnsOnlyNonDeletedEntities() {
    Exemplo active = new Exemplo("Ativo", "Registro visível");
    Exemplo deleted = new Exemplo("Deletado", "Registro removido");
    deleted.setEntityStatus(EntityStatus.DELETED);
    exemploRepository.saveAllAndFlush(Set.of(active, deleted));

    Set<Exemplo> result = exemploRepository.findAllByEntityStatusNot(EntityStatus.DELETED);

    assertThat(result).extracting(Exemplo::getNome).containsExactly("Ativo");
  }
}
