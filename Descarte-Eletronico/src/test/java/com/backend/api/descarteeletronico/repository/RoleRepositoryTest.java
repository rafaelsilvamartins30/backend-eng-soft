package com.backend.api.descarteeletronico.repository;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.role.Role;
import com.backend.api.descarteeletronico.model.role.RoleName;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
class RoleRepositoryTest {

  @Container
  static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:16-alpine")
          .withDatabaseName("descarte_eletronico_test")
          .withUsername("descarte")
          .withPassword("descarte");

  @Autowired private RoleRepository roleRepository;
  @Autowired private JdbcTemplate jdbcTemplate;

  @DynamicPropertySource
  static void configureDatasource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }

  @Test
  void flywayMigrationCreatesRolesTableAndSeedsAdminRole() {
    Boolean exists =
        jdbcTemplate.queryForObject(
            "select exists (select 1 from information_schema.tables where table_name = 'roles')",
            Boolean.class);

    assertThat(exists).isTrue();
    assertThat(roleRepository.existsByNomeAndEntityStatus(RoleName.ADMIN, EntityStatus.ACTIVE))
        .isTrue();
  }

  @Test
  void findByNomeAndEntityStatusReturnsActiveAdminRole() {
    Optional<Role> result =
        roleRepository.findByNomeAndEntityStatus(RoleName.ADMIN, EntityStatus.ACTIVE);

    assertThat(result).isPresent();
    assertThat(result.get().getNome()).isEqualTo(RoleName.ADMIN);
  }

  @Test
  void findByIdAndEntityStatusIgnoresDeletedEntity() {
    Role deleted = roleRepository.findByNomeAndEntityStatus(RoleName.ADMIN, EntityStatus.ACTIVE).orElseThrow();
    deleted.setEntityStatus(EntityStatus.DELETED);
    Role saved = roleRepository.saveAndFlush(deleted);

    Optional<Role> result = roleRepository.findByIdAndEntityStatus(saved.getId(), EntityStatus.ACTIVE);

    assertThat(result).isEmpty();
  }
}
