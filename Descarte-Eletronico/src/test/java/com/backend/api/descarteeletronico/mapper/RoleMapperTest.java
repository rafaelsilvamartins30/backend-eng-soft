package com.backend.api.descarteeletronico.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.role.Role;
import com.backend.api.descarteeletronico.model.role.RoleName;
import com.backend.api.descarteeletronico.model.role.dto.RoleResponse;
import java.util.Set;
import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class RoleMapperTest {

  private final RoleMapper mapper = Mappers.getMapper(RoleMapper.class);

  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class MapeamentoParaDTO {

    @Test
    @Order(1)
    void toResponseMapsRoleToResponse() {
      Role role = new Role(RoleName.ADMIN);

      RoleResponse response = mapper.toResponse(role);

      assertThat(response.nome()).isEqualTo(RoleName.ADMIN);
    }

    @Test
    @Order(2)
    void toResponseSetMapsSet() {
      Role role = new Role(RoleName.ADMIN);

      Set<RoleResponse> responses = mapper.toResponseSet(Set.of(role));

      assertThat(responses).hasSize(1);
      assertThat(responses.iterator().next().nome()).isEqualTo(RoleName.ADMIN);
    }

    @Test
    @Order(3)
    void toResponseReturnsNullWhenInputIsNull() {
      assertThat(mapper.toResponse(null)).isNull();
    }
  }
}
