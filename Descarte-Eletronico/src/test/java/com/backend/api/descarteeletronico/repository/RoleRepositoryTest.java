package com.backend.api.descarteeletronico.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.role.Role;
import com.backend.api.descarteeletronico.model.role.RoleName;
import java.util.Optional;
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
@DisplayName("RoleRepository - Testes de Persistência")
class RoleRepositoryTest extends BaseRepositoryTest {

  @Autowired private RoleRepository roleRepository;
  @Autowired private JdbcTemplate jdbcTemplate;

  @Nested
  @Order(1)
  @DisplayName("Persistência")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class PersistenceTests {

    @Test
    @Order(1)
    @DisplayName("Deve garantir que a migration do Flyway criou a tabela roles e populou ADMIN")
    void flywayMigrationCreatesRolesTableAndSeedsAdminRole() {
      Boolean exists =
          jdbcTemplate.queryForObject(
              "select exists (select 1 from information_schema.tables where table_name = 'roles')",
              Boolean.class);

      assertThat(exists).isTrue();
      assertThat(roleRepository.existsByNomeAndEntityStatus(RoleName.ADMIN, EntityStatus.ACTIVE))
          .isTrue();
    }
  }

  @Nested
  @Order(2)
  @DisplayName("Consultas Customizadas")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class CustomQueryTests {

    @Test
    @Order(1)
    @DisplayName("Deve buscar role ADMIN ativa por nome")
    void findByNomeAndEntityStatusReturnsActiveAdminRole() {
      Optional<Role> result =
          roleRepository.findByNomeAndEntityStatus(RoleName.ADMIN, EntityStatus.ACTIVE);

      assertThat(result).isPresent();
      assertThat(result.get().getNome()).isEqualTo(RoleName.ADMIN);
    }

    @Test
    @Order(2)
    @DisplayName("Deve ignorar role excluída na busca por ID")
    void findByIdAndEntityStatusIgnoresDeletedEntity() {
      Role deleted =
          roleRepository.findByNomeAndEntityStatus(RoleName.ADMIN, EntityStatus.ACTIVE).orElseThrow();
      deleted.setEntityStatus(EntityStatus.DELETED);
      Role saved = roleRepository.saveAndFlush(deleted);

      Optional<Role> result =
          roleRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

      assertThat(result).isEmpty();
    }
  }
}
