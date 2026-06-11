package com.backend.api.descarteeletronico.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.usuario.Usuario;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import java.util.Set;
import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class UsuarioMapperTest {

  private final UsuarioMapper mapper = Mappers.getMapper(UsuarioMapper.class);

  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class MapeamentoParaDTO {

    @Test
    @Order(1)
    void toResponseMapsUsuarioToResponse() {
      Usuario usuario = new Usuario("Admin", "admin@test.com", "password");
      usuario.setRoles(Set.of());

      UsuarioResponse response = mapper.toResponse(usuario);

      assertThat(response.nome()).isEqualTo(usuario.getNome());
      assertThat(response.email()).isEqualTo(usuario.getEmail());
    }

    @Test
    @Order(2)
    void toResponseReturnsNullWhenInputIsNull() {
      assertThat(mapper.toResponse(null)).isNull();
    }
  }
}
